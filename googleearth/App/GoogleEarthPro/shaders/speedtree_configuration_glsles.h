// Copyright 2010 Google, Inc.  All rights reserved.
// Author: chrisco@google.com (Chris Co)
//
// NOTE(chrisco) 2010-01-27: Since much of this shader code remains untouched
// from the way it was provided by IDV, the IDV copyright is kept here.

#ifndef EARTH_CLIENT_LIBS_EVLL_SPEEDTREE_SHADERS_SOURCE_SPEEDTREE_CONFIGURATION_H_
#define EARTH_CLIENT_LIBS_EVLL_SPEEDTREE_SHADERS_SOURCE_SPEEDTREE_CONFIGURATION_H_

///////////////////////////////////////////////////////////////////////
//  SpeedTree.fx
//
//  SpeedTree.fx is the file passed to the shader compiler.  It includes the other
//  SpeedTree FX/Cg files.  These shaders will compile with HLSL and Cg compilers.
//
//
//  *** INTERACTIVE DATA VISUALIZATION (IDV) CONFIDENTIAL AND PROPRIETARY INFORMATION ***
//
//  This software is supplied under the terms of a license agreement or
//  nondisclosure agreement with Interactive Data Visualization, Inc. and
//  may not be copied, disclosed, or exploited except in accordance with
//  the terms of that agreement.
//
//      Copyright (c) 2003-2009 IDV, Inc.
//      All rights reserved in all media.
//
//      IDV, Inc.
//      http://www.idvinc.com

// Define macros that configure the SpeedTree 5.0 shaders for Google Earth.
//#define GOOGLE_COMPUTE_FOG

// Always use the SpeedTree's shader compiler settings for DirectX 9.
//#define SPEEDTREE_OPENGL
#define SPEEDTREE_DIRECTX9

// Set up #defines to configure shaders
//#define SPEEDTREE_SHADOW_MAP_RES        1024
#define SPEEDTREE_Z_UP
//#define SPEEDTREE_NUM_SHADOW_MAPS       1
//#define SPEEDTREE_TERRAIN_SHOW_SPLITS
//#define SPEEDTREE_BASIC_WIND
//#define SPEEDTREE_WORLD_TRANSLATE
#define SPEEDTREE_HORZ_BBS_ACTIVE
//#define SPEEDTREE_DIRECTIONAL_WIND
//#define SPEEDTREE_SMOOTH_SHADOWS
#define SPEEDTREE_MAX_BILLBOARD_IMAGES  50
//#define SPEEDTREE_ALPHA_TESTING
//#define SPEEDTREE_SHADOW_MIPMAPPED
//#define SPEEDTREE_FROND_RIPPLING
#define SPEEDTREE_RHCS

// A few lighting macros
//#define SPEEDTREE_AMBIENT_CONTRAST


#define REG(x)// : register(c##x)


///////////////////////////////////////////////////////////////////////
//  Shadow #defines

#ifndef SPEEDTREE_SMOOTH_SHADOWS
    #define SPEEDTREE_SMOOTH_SHADOWS false
#endif
#ifdef SPEEDTREE_SHADOW_MAP_RES
    #define SPEEDTREE_SHADOWS_ENABLED
#endif
#define SPEEDTREE_CLIP_OFFSET 0.1

// some experimental settings; use as a basis for your own experimentation
//#define SPEEDTREE_WIND_AFFECTS_LIGHTING
//#define SPEEDTREE_BILLBOARDS_RECEIVE_SHADOWS


///////////////////////////////////////////////////////////////////////
//  Lighting default #defines

// NOTE(chrisco) 2010-01-27: Do not define undefined macros.
#if 0
#ifndef SPEEDTREE_AMBIENT_CONTRAST
    #define SPEEDTREE_AMBIENT_CONTRAST false
#endif
#endif

// NOTE(chrisco) 2010-01-20: Never define these SpeedTree globals.
// Earth/Alchemy binds them as uniforms for the shaders in a different way.
#if 0
///////////////////////////////////////////////////////////////////////
//  Common Global Variables
//
//  These are all global variables that are shared by more than one of
//  the main shader types (branch, frond, leaf, and billboard).  The constant
//  registers are specified after many of the global parameters in order
//  to ensure that the variables will be successfully shared even when the
//  shaders are compiled off-line separately.
//
//  Note: What we call "composite maps" or "composite textures" are also known
//        as "texture atlases"

mat4    g_mModelViewProj        REG(0);     // composite modelview/projection matrix
vec4      g_vTreePosAndScale      REG(4);     // xyz = position, w = scale
vec4      g_vTreeRotation         REG(5);     // xyz = rotation values, w = lod value (1 = highest, 0 = lowest)
vec4      g_vLodProfile           REG(6);     // x = high 3D LOD distance, y = low 3D LOD distance, z = billboard start distance, w = billboard final distance
mat4    g_mMaterial             REG(7);     // [0] = ambient  (material_ambient * light_ambient)
                                                // [1] = diffuse  (material_diffuse * light_diffuse)
                                                // [2] = specular (material_specular * light_specular), w = shininess [0-128]
                                                // [3] = emissive (material_emissive * light_emissive)
vec4      g_vMaterialExtras       REG(11);    // x = light scalar, y = alpha scalar, z = shininess, w = 1.0 - ambient_contrast
vec2      g_vMaterialTransmission REG(12);    // x = shadow darkness, y = view dependence
vec3      g_vLightDir             REG(13);    // light direction
vec3      g_vSkyColor             REG(14);    // rgb = color, a = horizon offset
vec4      g_vFogParams            REG(15);    // x = fog_end, .y = fog_end - fog_start, z = -min sky fog, w = -1.0 / sky fog range
vec3      g_vFogColor             REG(16);    // rgb fog color
vec3      g_vSunColor             REG(17);    // rgb sun color
vec3      g_vSunParams            REG(18);    // x = size, y = spread exponent, z = fog bloom
vec3      g_vCameraPosition       REG(19);    // camera/eye location in world space
vec3      g_vCameraDirection      REG(20);    // camera/eye direction in world space
vec2      g_vCameraAngles         REG(21);    // shared by Billboard.fx and Leaf.fx - stores camera azimuth and pitch for billboarding computations
float       g_fFarClip              REG(22);    // far clipping plane distance (visibility)
float       g_fHorzBillboardFade    REG(23);    // 0 = horz bbs are transparent, 1 = they are opaque
vec4      g_vSplatMapTiles        REG(24);    // x = splat0 tile, y = splat1 tile, z = splat2 tile, w = normal map blue scalar
vec4      g_vSplitDistances       REG(25);    // x = shadow_map_0_end, y = shadow_map_1_end, etc.
vec3      g_vShadowFadeParams     REG(26);    // x = shadow fade %, y = 1/(fade% - end), z = 1/(end - fade%); all to reduce pixel shader instructions

// wind
vec3      g_vWindDir              REG(27);    // xyz = dir
vec4      g_vWindTimes            REG(28);    // x = primary, y = secondary, z = frond, w = leaves
vec4      g_vWindDistances        REG(29);    // x = primary osc, y = secondary osc, z = wind height, w = height exponent
vec3      g_vWindLeaves           REG(30);    // x = distance, y = leaves lighting change, z = leaves windward scalar
vec3      g_vWindFrondRipple      REG(31);    // x = amount, y = u tile, z = v tile
vec3      g_vWindGust             REG(32);    // x = combined wind strength, y = primary distance, z = gust scale
vec3      g_vWindGustHints        REG(33);    // x = vertical offset %, y = wind dir adjustment, z = unison

// variable-sized shadow
#ifdef SPEEDTREE_SHADOWS_ENABLED
    mat4    g_amLightViewProjs[SPEEDTREE_NUM_SHADOW_MAPS] REG(34);
    float       g_afSplitDistances[SPEEDTREE_NUM_SHADOW_MAPS];
#endif

#endif

#endif  // EARTH_CLIENT_LIBS_EVLL_SPEEDTREE_SHADERS_SOURCE_SPEEDTREE_CONFIGURATION_H_
