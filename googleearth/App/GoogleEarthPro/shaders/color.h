////////////////////////////////////////////////////////////////////////
// CIE Xyz and Lab color space functions ///////////////////////////////
//
// These functions are ported to GLSL from:
//    google3/image/content/color/lab/lab.cc.
//
// The constant kOffset was renamed to kCielabOffset to make water
// surface shader compile, and F() was renamed to CielabF().

#define kGamma 2.4
#define kA 0.055
#define kDelta (6.0 / 29.0)
#define kThreeDeltaSquared (3.0 * 36.0 / 841.0)
#define kCielabOffset (16.0 / 116.0)
#define kD65Cie1931 vec3(0.950472, 0.999999, 1.088966)

// Uniform to control color desaturation.
uniform lowp vec4 colorDesaturation;

float SrgbGammaExpandFast(float k) {
  float one_plus_a_reciprocal = 1.0 / (1.0 + kA);
  if (k > 0.04045) {
    return pow((k + kA) * one_plus_a_reciprocal, kGamma);
  } else {
    return k * 0.077399;
  }
}

vec3 ConvertSrgbToXyzFast(vec3 srgb) {
  vec3 xyz = vec3(0.0);
  float gr = SrgbGammaExpandFast(srgb.x);
  float gg = SrgbGammaExpandFast(srgb.y);
  float gb = SrgbGammaExpandFast(srgb.z);
  xyz.x =
    0.4123955889674142 * gr +
    0.3575834307637148 * gg +
    0.1804926473817016 * gb;
  xyz.y =
    0.2125862307855956 * gr +
    0.7151703037034109 * gg +
    0.0722004986433362 * gb;
  xyz.z =
    0.0192972154917469 * gr +
    0.1191838645808486 * gg +
    0.9504971251315799 * gb;
  return xyz;
}

// Implements the Wikipedia "Lab color space" formula for f()
// under "XYZ to CIE L*a*b* (CIELAB) and CIELAB to XYZ conversions; the
// forward transformation."
float CielabF(float t) {
  float one_third = 1.0 / 3.0;
  if (t > 0.008856) {
    return pow(t, one_third);
  } else {
    return 7.787 * t + kCielabOffset;
  }
}

// This function implements the Wikipedia "Lab color space" formulas under
// "XYZ to CIE L*a*b* (CIELAB) and CIELAB to XYZ conversions; The forward
// transformation."
vec3 ConvertXyzToCielab(vec3 xyz,
                        vec3 illuminant) {
  vec3 lab = vec3(0.0);
  float f1 = CielabF(xyz.y / illuminant.y);
  lab.x = 116.0 * f1 - 16.0;
  lab.y = 500.0 * (CielabF(xyz.x / illuminant.x) - f1);
  lab.z = 200.0 * (f1 - CielabF(xyz.z / illuminant.z));
  return lab;
}

vec3 ConvertSrgbToLabFast(vec3 srgb) {
  vec3 xyz = ConvertSrgbToXyzFast(srgb);
  vec3 lab = ConvertXyzToCielab(xyz, kD65Cie1931);
  lab /= 255.0;
  return lab;
}

// This function implements the Wikipedia "Lab color space" formulas under
// "XYZ to CIE L*a*b* (CIELAB) and CIELAB to XYZ conversions; The reverse
// transformation."
vec3 ConvertCielabToXyz(vec3 lab,
                        vec3 illuminant) {
  lab *= 255.0;
  vec3 xyz = vec3(0.0);
  float rx = 1.0 / 500.0, ry = 1.0 / 116.0, rz = 1.0 / 200.0;
  float f_y = (lab[0] + 16.0) * ry;
  float f_x = f_y + lab[1] * rx,
         f_z = f_y - lab[2] * rz;

  if (f_y > kDelta) {
    xyz[1] = illuminant[1] * pow(f_y, 3.0);
  } else {
    xyz[1] = illuminant[1] * (f_y - kCielabOffset) *
             kThreeDeltaSquared;
  }

  if (f_x > kDelta) {
    xyz[0] = illuminant[0] * pow(f_x, 3.0);
  } else {
    xyz[0] = illuminant[0] * (f_x - kCielabOffset) *
             kThreeDeltaSquared;
  }

  if (f_z > kDelta) {
    xyz[2] = illuminant[2] * pow(f_z, 3.0);
  } else {
    xyz[2] = illuminant[2] * (f_z - kCielabOffset) *
             kThreeDeltaSquared;
  }
  return xyz;
}

// Implements the Wikipedia "sRGB color space" formula for gamma compression
// under "Specification of the transformation; The forward transformation
// (CIE xyY or CIE XYZ to sRGB)."
float SrgbGammaCompress(float c) {
  float c_prime = 0.0;
  if (c <= 0.0031308) {
    c_prime = 12.92 * c;
  } else {
    c_prime = (1.0 + kA) * pow(c, 1.0 / kGamma) - kA;
  }
  return c_prime;
}

// Implements the Wikipedia "sRGB color space" formulas under
// "Specification of the transformation; The forward transformation
// (CIE xyY or CIE XYZ to sRGB)."
vec3 ConvertXyzToSrgb(vec3 xyz) {
  float
    er = 3.2406 * xyz[0] + -1.5372 * xyz[1] + -0.4986 * xyz[2],
    eg = -0.9689 * xyz[0] + 1.8758 * xyz[1] + 0.0415 * xyz[2],
    eb = 0.0557 * xyz[0] + -0.2040 * xyz[1] + 1.0570 * xyz[2];
  return vec3(SrgbGammaCompress(er),
              SrgbGammaCompress(eg),
              SrgbGammaCompress(eb));
}

// CIE Xyz and Lab color space functions ///////////////////////////////
////////////////////////////////////////////////////////////////////////

// Desaturates color by ammount (0.0 is full color, 1.0 is greyscale).
void ApplyColorDesaturation(inout vec4 color, float ammount) {
  vec3 labcolor = ConvertSrgbToLabFast(color.rgb);
  labcolor.gb = vec2(0.0);
  labcolor = ConvertXyzToSrgb(ConvertCielabToXyz(labcolor, kD65Cie1931));
  color.rgb = mix(color.rgb, labcolor, ammount);
}
