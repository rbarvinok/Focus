///////////////////////////////////////////////////////////////////////////////
// Shader Name: stbillboard
// Copyright 2007 Google Inc. All Rights Reserved.
// Reworked into GE/Alchemy by: dfalcantara@google.com (Dan Alcantara)
//
// Part of a pair of 360 billboard shaders.  Each chooses a different texture
// to display on their billboard.
///////////////////////////////////////////////////////////////////////////////

#include <stcommonobjects.ini>

///////////////////////////////////////////////////////////////////////////////
[INTERFACE]
section  = stbillboard

[stbillboard]
attrs=

[CONFIGURATION]
fileName       = stbillboard.cfg
implementation = hardware_shader_implementation


///////////////////////////////////////////////////////////////////////////////
//  PASS DECLARATIONS
//    Most of the attributes are contained in STCommonObjects.ini.
//    FakeLightDirection is set to be a static direction pointing downwards.
///////////////////////////////////////////////////////////////////////////////
[single_pass]
attrs = enableVertexShaderATTR,bindVertexShader,\
        enablePixelShaderATTR,bindPixelShader


///////////////////////////////////////////////////////////////////////////////
// VERTEX SHADER
///////////////////////////////////////////////////////////////////////////////
[bindVertexShader]
type                = igVertexShaderBindAttr
fields              = _vertexShader
_vertexShader.value = vertexShader

[vertexShader]
type                  = igVertexShaderAttr
fields                = _textDefinition,_entryPoint,_stateList
_textDefinition.file  = stbillboard.arbvp1
_entryPoint.value     = vmain
_stateList.value      = batch_model_view_proj_VS, \
                        batch_camera_params_VS,   \
                        batch_camera_location_VS, \
                        lightDirection_VS,        \
                        lod_profile_VS,           \
                        billboard_constants_VS


///////////////////////////////////////////////////////////////////////////////
// PIXEL SHADER
///////////////////////////////////////////////////////////////////////////////
[bindPixelShader]
type                = igPixelShaderBindAttr
fields              = _pixelShader
_pixelShader.value  = pixelShader

[pixelShader]
type                  = igPixelShaderAttr
fields                = _textDefinition,_entryPoint,_samplerList
_textDefinition.file  = stbillboard.arbfp1
_entryPoint.value     = pmain
_samplerList.value    = diffuseMap_PS,  \
                        normalMap_PS

