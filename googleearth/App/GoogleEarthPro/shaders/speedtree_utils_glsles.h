#define mul(x,y) ((x)*(y))
#define sincos(a, s, c) { s = sin(a); c = cos(a); }
#define trunc(x) ((x) < 0.0 ? -floor(-(x)) : floor(x))
#define lerp(a, b, w) ((a) + (w)*((b)-(a)))
#if defined(FORCE_NO_ALPHA_TEST)
#define clip(x) { }
#else
#define clip(x) { if ((x) < 0.0) discard; }
#endif
///////////////////////////////////////////////////////////////////////
//  Constants

// NOTE(chrisco) 2010-01-26: c_fBillboardAlphaScalar and c_fMaxAlphaScalar
// appear to be unused.
#if 0   // begin ignore unused constants
float c_fBillboardAlphaScalar = 1.7;
float c_fMaxAlphaScalar = 3.0;   // max amount to scale texture's alpha values to make
                                        // alpha-to-coverage work cleanly with our textures
#endif  // end ignore unused constants
#define c_fBillboardNoiseScalar     800.0
#define c_f3dGeometryNoiseScalar    100.0
#define c_fTwoPi                    6.28318530
#define c_fPi                       3.141592653


///////////////////////////////////////////////////////////////////////
//  ComputeSpecularHalfVector

vec3 ComputeSpecularHalfVector(vec3 vNormal, vec3 vBinormal, vec3 vTangent, vec3 vPosition,
                                 vec3 g_vCameraPosition, vec3 g_vLightDir)
{
    // compute half vector for specular lighting
    vec3 vHalfVector = normalize(normalize(g_vCameraPosition.xyz - vPosition.xyz) + g_vLightDir);

    // convert half vector to tangent space
    mat3 matTangentSpaceMatrix = mat3(vTangent, vBinormal, vNormal);

    return mul(matTangentSpaceMatrix, vHalfVector);
}


///////////////////////////////////////////////////////////////////////
//  Fade

float Fade(float fValue, float fStart, float fEnd)
{
    float fFade = saturate((fValue - fStart) / (fEnd - fStart));

    return fFade;
}


// NOTE(chrisco) 2010-01-26: This version of Fade() appears to be unused.
#if 0   // begin ignore duplicate Fade()
///////////////////////////////////////////////////////////////////////
//  Fade

float Fade(float fValue, vec2 vRange)
{
    float c_fStart = vRange.x;
    float c_fEnd = vRange.y;

    return saturate((fValue - c_fStart) / (c_fEnd - c_fStart));
}
#endif    // end ignore duplicate Fade()


///////////////////////////////////////////////////////////////////////
//  AlphaTestNoise

float AlphaTestNoise(vec2 vTexCoords)
{
    return abs(fract(vTexCoords.x * vTexCoords.y));
}


///////////////////////////////////////////////////////////////////////
//  EncodeVectorToColor

vec3 EncodeVectorToColor(vec3 vVector)
{
    return vVector * 0.5 + 0.5;
}


///////////////////////////////////////////////////////////////////////
//  DecodeVectorFromColor

vec3 DecodeVectorFromColor(vec3 vVector)
{
    return vVector * 2.0 - 1.0;
}


#ifdef GOOGLE_COMPUTE_FOG
///////////////////////////////////////////////////////////////////////
//  FogPixel

vec3 FogPixel(vec4 vFogData, vec3 vColor)
{
    return lerp(vFogData.xyz, vColor, vFogData.w);
}
#endif


///////////////////////////////////////////////////////////////////////
//  ProjectToScreen_NoTranslation

vec4 ProjectToScreen_NoTranslation(vec3 vPos,
                                     mat4 g_mModelViewProj)
{
    return mul(g_mModelViewProj, vec4(vPos, 1.0));
}


///////////////////////////////////////////////////////////////////////
//  ProjectToScreen

vec4 ProjectToScreen(vec3 vPos,
                       vec3 g_vCameraPosition, mat4 g_mModelViewProj)
{
#ifdef SPEEDTREE_WORLD_TRANSLATE
    vPos -= g_vCameraPosition;
#endif

    return ProjectToScreen_NoTranslation(vPos, g_mModelViewProj);
}


///////////////////////////////////////////////////////////////////////
//  ComputeBillboardFade
//
//  Use to fade the geometry to billboard images

// shouldn't be higher than 0.5
#define c_fOffsetLength 0.45

float ComputeBillboardFade(float fDistance,
                           vec4 g_vLodProfile)
{
    float fOffset = c_fOffsetLength * (g_vLodProfile.w - g_vLodProfile.z);
    float fStart = g_vLodProfile.z;
    float fEnd = g_vLodProfile.w - fOffset;

    return Fade(fDistance, fStart, fEnd);
}


///////////////////////////////////////////////////////////////////////
//  Compute3dFade
//
//  Use to fade the geometry to billboard images

float Compute3dFade(out float fDistance,
                    float c_fAlphaScalar, vec3 g_vCameraPosition,
                    vec4 g_vTreePosAndScale, vec4 g_vLodProfile)
{
    fDistance = distance(g_vCameraPosition.xyz, g_vTreePosAndScale.xyz);

    float fOffset = c_fOffsetLength * (g_vLodProfile.w - g_vLodProfile.z);
    float fStart = g_vLodProfile.z + fOffset;
    float fEnd = g_vLodProfile.w;

    return c_fAlphaScalar * (1.0 - Fade(fDistance, fStart, fEnd));
}


///////////////////////////////////////////////////////////////////////
//  RotationMatrix_zAxis
//
//  Constructs a Z-axis rotation matrix

mat3 RotationMatrix_zAxis(float fAngle)
{
    // compute sin/cos of fAngle
    vec2 vSinCos;
    sincos(fAngle, vSinCos.x, vSinCos.y);

    return mat3(vSinCos.y, vSinCos.x, 0.0,
                    -vSinCos.x, vSinCos.y, 0.0,
                    0.0, 0.0, 1.0);
}


///////////////////////////////////////////////////////////////////////
//  Rotate_zAxis
//
//  Returns an updated .xy value

vec2 Rotate_zAxis(float fAngle, vec3 vPoint)
{
    vec2 vSinCos;
    sincos(fAngle, vSinCos.x, vSinCos.y);

    return vec2(dot(vSinCos.yx, vPoint.xy), dot(vec2(-vSinCos.x, vSinCos.y), vPoint.xy));
}


///////////////////////////////////////////////////////////////////////
//  RotationMatrix_yAxis
//
//  Constructs a Y-axis rotation matrix

mat3 RotationMatrix_yAxis(float fAngle)
{
    // compute sin/cos of fAngle
    vec2 vSinCos;
    sincos(fAngle, vSinCos.x, vSinCos.y);

    return mat3(vSinCos.y, 0.0, -vSinCos.x,
                    0.0, 1.0, 0.0,
                    vSinCos.x, 0.0, vSinCos.y);
}


///////////////////////////////////////////////////////////////////////
//  Rotate_yAxis
//
//  Returns an updated .xz value

vec2 Rotate_yAxis(float fAngle, vec3 vPoint)
{
    vec2 vSinCos;
    sincos(fAngle, vSinCos.x, vSinCos.y);

    return vec2(dot(vec2(vSinCos.y, -vSinCos.x), vPoint.xz), dot(vSinCos.xy, vPoint.xz));
}


///////////////////////////////////////////////////////////////////////
//  RotationMatrix_xAxis
//
//  Constructs a X-axis rotation matrix

mat3 RotationMatrix_xAxis(float fAngle)
{
    vec2 vSinCos;
    sincos(fAngle, vSinCos.x, vSinCos.y);

    return mat3(1.0, 0.0, 0.0,
                    0.0, vSinCos.y, vSinCos.x,
                    0.0, -vSinCos.x, vSinCos.y);
}


///////////////////////////////////////////////////////////////////////
//  Rotate_xAxis
//
//  Returns an updated .yz value

vec2 Rotate_xAxis(float fAngle, vec3 vPoint)
{
    vec2 vSinCos;
    sincos(fAngle, vSinCos.x, vSinCos.y);

    return vec2(dot(vSinCos.yx, vPoint.yz), dot(vec2(-vSinCos.x, vSinCos.y), vPoint.yz));
}


///////////////////////////////////////////////////////////////////////
//  Rotate_UpAxis

vec3 Rotate_UpAxis(float fAngle, vec3 vPoint)
{
#ifdef SPEEDTREE_Z_UP
    return vec3(Rotate_zAxis(fAngle, vPoint), vPoint.z);
#else
    vec2 vRotatedVector = Rotate_yAxis(fAngle, vPoint);
    return vec3(vRotatedVector.x, vPoint.y, vRotatedVector.y);
#endif
}


///////////////////////////////////////////////////////////////////////
//  Rotate_OutAxis

vec3 Rotate_OutAxis(float fAngle, vec3 vPoint)
{
#ifdef SPEEDTREE_Z_UP
    vec2 vRotatedVector = Rotate_yAxis(fAngle, vPoint);
    return vec3(vRotatedVector.x, vPoint.y, vRotatedVector.y);
#else
    return vec3(Rotate_zAxis(fAngle, vPoint), vPoint.z);
#endif
}


///////////////////////////////////////////////////////////////////////
//  RotationMatrix_AroundUpAxis
//
//  Up axis changes depending on the coordinate system used by the client application

mat3 RotationMatrix_AroundUpAxis(float fAngle)
{
#ifdef SPEEDTREE_Z_UP
    return RotationMatrix_zAxis(fAngle);
#else
    return RotationMatrix_yAxis(fAngle);
#endif
}


///////////////////////////////////////////////////////////////////////
//  RotationMatrix_AroundOutAxis
//
//  Up axis changes depending on the coordinate system used by the client application

mat3 RotationMatrix_AroundOutAxis(float fAngle)
{
#ifdef SPEEDTREE_Z_UP
    return RotationMatrix_yAxis(fAngle);
#else
    return RotationMatrix_zAxis(fAngle);
#endif
}


///////////////////////////////////////////////////////////////////////
//  ConvertToStd

vec3 ConvertToStd(vec3 vCoord)
{
#ifdef SPEEDTREE_Z_UP
    #ifdef SPEEDTREE_LHCS
        return vec3(vCoord.x, -vCoord.y, vCoord.z);
    #else
        return vCoord;
    #endif
#else
    #ifdef SPEEDTREE_LHCS
        return vec3(vCoord.x, vCoord.z, vCoord.y);
    #else
        return vec3(vCoord.x, -vCoord.z, vCoord.y);
    #endif
#endif
}


///////////////////////////////////////////////////////////////////////
//  ConvertFromStd

vec3 ConvertFromStd(vec3 vCoord)
{
#ifdef SPEEDTREE_Z_UP
    #ifdef SPEEDTREE_LHCS
        return vec3(vCoord.x, -vCoord.y, vCoord.z);
    #else
        return vCoord;
    #endif
#else
    #ifdef SPEEDTREE_LHCS
        return vec3(vCoord.x, vCoord.z, vCoord.y);
    #else
        return vec3(vCoord.x, vCoord.z, -vCoord.y);
    #endif
#endif
}


#if 0   // begin ignore terrain code
///////////////////////////////////////////////////////////////////////
//  TerrainNormalMapLookup

vec3 TerrainNormalMapLookup(vec2 vTexCoords)
{
    // setup aliases (taken from global variables)
    float c_fNormalMapBlueScale = g_vSplatMapTiles.w;

    // normal map
    vec3 texNormal = SampleTexture(TerrainNormalMap, vTexCoords).xyz;
    texNormal -= 0.5;
    texNormal.b *= c_fNormalMapBlueScale;

#ifdef SPEEDTREE_LHCS
    #ifdef SPEEDTREE_Y_UP
        texNormal.xyz = texNormal.xzy;
    #else
        texNormal.y *= -1.0;
    #endif
#else
    #ifdef SPEEDTREE_Y_UP
        texNormal.xyz = texNormal.xzy;
        texNormal.z *= -1.0;
    #endif
#endif

    return normalize(texNormal);
}


///////////////////////////////////////////////////////////////////////
//  TerrainNormalMapLookupVS

vec3 TerrainNormalMapLookupVS(vec2 vTexCoords)
{
    // setup aliases (taken from global variables)
    float c_fNormalMapBlueScale = g_vSplatMapTiles.w;

    // normal map
    vec3 texNormal = SampleTextureLod(TerrainNormalMap, vTexCoords, 0.0).xyz;
    texNormal -= 0.5;
    texNormal.b *= c_fNormalMapBlueScale;

#ifdef SPEEDTREE_LHCS
    #ifdef SPEEDTREE_Y_UP
        texNormal.xyz = texNormal.xzy;
    #else
        texNormal.y *= -1.0;
    #endif
#else
    #ifdef SPEEDTREE_Y_UP
        texNormal.xyz = texNormal.xzy;
        texNormal.x *= -1.0;
    #endif
#endif

    return normalize(texNormal);
}
#endif  // end ignore terrain code

///////////////////////////////////////////////////////////////////////
//  AdjustForTreeRotation
//
//  Each instance can be rotated about its up-axis arbitrarily.  This function
//  performs quick rotations for positions, tangent vectors, etc.  It also
//  compensates for the current coordinate system.

vec3 AdjustForTreeRotation(vec3 vComponent,
                             vec4 g_vTreeRotation)
{
    vec4 vRotationValues = vec4(g_vTreeRotation.xy, -g_vTreeRotation.x, 0.0);

#ifdef SPEEDTREE_LHCS
    #ifdef SPEEDTREE_Z_UP
        return vec3(dot(vRotationValues.yxw, vComponent.xyz), dot(vRotationValues.zyw, vComponent.xyz), vComponent.z);
    #else
        return vec3(dot(vRotationValues.ywz, vComponent.xyz), vComponent.y, dot(vRotationValues.xwy, vComponent.xyz));
    #endif
#else // must be SPEEDTREE_RHCS
    #ifdef SPEEDTREE_Z_UP
        return vec3(dot(vRotationValues.yxw, vComponent.xyz), dot(vRotationValues.zyw, vComponent.xyz), vComponent.z);
    #else
        return vec3(dot(vRotationValues.ywz, vComponent.xyz), vComponent.y, dot(vRotationValues.xwy, vComponent.xyz));
    #endif
#endif // SPEEDTREE_LHCS
}

#if 0   // begin ignore sky code
///////////////////////////////////////////////////////////////////////
//  SkyColor
//
// The direction gives you a point in the sphere around the camera. The
// sky color at this point is computed using an s-curve function using the normalized
// up component. An s-curve function is 1 / (1 - e^-t), with the basic range being
// -6 to 6. Sun color is added on top of this.

vec3 SkyColor(vec3 vDirection)
{
#ifdef SPEEDTREE_Z_UP
    float fInterp = vDirection.z;
#else
    float fInterp = vDirection.y;
#endif

    // sigmoid (s-curve) function to ensure smooth sky blending
    fInterp = 12.0 * ((fInterp + g_vFogParams.z) * g_vFogParams.w + 0.5);
    fInterp = 1.0 / (1.0 + exp(fInterp));
    vec3 vSkyWithFog = lerp(g_vFogColor.xyz, g_vSkyColor.xyz, fInterp);

    float fSunLight = saturate(dot(vDirection, g_vLightDir) + g_vSunParams.x);

    fSunLight = pow(fSunLight, g_vSunParams.y);
    vec3 vTotalColor = lerp(vSkyWithFog, g_vSunColor, fSunLight);

    return vTotalColor;
}
#endif  // end ignore sky code

#ifdef GOOGLE_COMPUTE_FOG
///////////////////////////////////////////////////////////////////////
//  FogVertex

vec4 FogVertex(vec3 vWorldPosition,
                 vec3 g_vCameraPosition, vec3 g_vCameraDirection,
                 vec3 g_vLightDir, vec4 g_vFogParams, vec3 g_vSunParams)
{
    vec3 vSkyDirection = vWorldPosition - g_vCameraPosition;
    float fDistance = length(vSkyDirection);
    vSkyDirection = vSkyDirection / fDistance;

    float fFogValue = saturate((g_vFogParams.x - fDistance) / g_vFogParams.y);

    float fSunlight = (g_vSunParams.z * dot(-g_vCameraDirection, -g_vLightDir) + 1.0);
    fSunlight *= fSunlight;
    fSunlight = saturate(fSunlight);

    return vec4(SkyColor(vSkyDirection), fFogValue * fSunlight);
}
#endif


///////////////////////////////////////////////////////////////////////
//  SpecialEffectFade
//
//  Used to fade the specular and transmission effect so that the 3D tree
//  will better match the billboards (that don't have these special effects).

float SpecialEffectFade(float fDistance,
                        vec4 g_vLodProfile)
{
    return 1.0;
}

///////////////////////////////////////////////////////////////////////
//  PossibleEarlyOut

#ifdef GE_PIXEL_SHADER
void PossibleEarlyOut(float fAlpha)
{
#ifndef SPEEDTREE_Z_PREPASS
#if defined(SPEEDTREE_ALPHA_TESTING)
    clip(fAlpha - SPEEDTREE_CLIP_OFFSET);
#endif  // defined(SPEEDTREE_ALPHA_TESTING)
#endif
}

void PossibleEarlyOut(float fAlpha, float fOffset)
{
#ifndef SPEEDTREE_Z_PREPASS
#if defined(SPEEDTREE_ALPHA_TESTING)
    clip(fAlpha - fOffset);
#endif  // defined(SPEEDTREE_ALPHA_TESTING)
#endif
}

void DoAlphaTestIfNeeded(float fAlpha) {
#if !defined(SPEEDTREE_Z_PREPASS) && !defined(FORCE_NO_ALPHA_TEST)
// OGL ES 2.0 does not support alpha test, so need to do it in the
// shader.
#if !defined(SPEEDTREE_ALPHA_TESTING) && defined(GL_ES)
  // alpha_ref_value is set by igVisualContext, and will be -1 if alpha test
  // is off.  alpha_ref_value is declared in glsles.h.
  if (fAlpha < alpha_ref_value) {
    discard;
  }
#endif  // !defined(SPEEDTREE_ALPHA_TESTING) && defined(GL_ES)
#endif  // !defined(SPEEDTREE_Z_PREPASS) && !defined(FORCE_NO_ALPHA_TEST)
}

#endif
