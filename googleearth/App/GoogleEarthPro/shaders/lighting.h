// See atmosphere.glslesv for a discussion of has_lighting.  It is required for
// Diorama lighting to work with the atmosphere shaders.
// uniform float has_lighting;
uniform vec4 ig_LightDirectionalDirection[2];
uniform vec4 ig_LightDirectionalDiffuse[2];
uniform vec4 ig_SceneAmbientMaterial;
uniform vec4 global_color;
uniform float has_color;

varying lowp vec4 v_global_color;

vec4 ApplyModelLighting() {
  vec4 color;
  if (has_color == 1.0) {
#if defined(USE_IMPROVED_SHADER_VARIATION)
    color = vec4(1.0);
#else
    color = ig_Color * (1.0 / 255.0);
#endif
  } else {
    color = global_color;
  }

  if (has_lighting == 1.0) {
    vec3 light1_direction = ig_LightDirectionalDirection[0].rgb;
    vec3 light2_direction = ig_LightDirectionalDirection[1].rgb;
    vec3 light1_diffuse = ig_LightDirectionalDiffuse[0].rgb;
    vec3 light2_diffuse = ig_LightDirectionalDiffuse[1].rgb;
    vec3 normal = ig_Normal.xyz;
    normal = (ig_ModelViewMatrix * vec4(normal, 0.0)).rgb;

    // If the normal has zero length, calling normalize(normal) does
    // not work as expected when using ANGLE on some older video cards.
    // This causes problems even when has_lighting == 0.0
    // (see bug #7417492).
    float normal_length = length(normal);

    // 1e-16 was chosen kind of arbitrarily.  It requires highp floating
    // point operations, but all ES 2.0 vertex shaders should be able to
    // support highp (it's part of the spec).
    float inverse_normal_length = 1.0 / max(1.0e-16, normal_length);
    normal *= inverse_normal_length;

    // Always perform computation for 2 directional lights.
    // If only one light is enabled, the color and direction for the 2nd light
    // will be (0, 0, 0), so it will not contribute anything to the final color.
    vec3 light_diffuse = light1_diffuse *
        clamp(dot(normal, light1_direction), 0.0, 1.0);
    light_diffuse += light2_diffuse *
        clamp(dot(normal, light2_direction), 0.0, 1.0);

    // When lighting is on, the color contains the material color.
    color.rgb *= light_diffuse;

    // Add the ambient and emissive lighting components.
    color.rgb += ig_SceneAmbientMaterial.rgb;
    color.rgb = clamp(color.rgb, 0.0, 1.0);
  }
  return color;
}

