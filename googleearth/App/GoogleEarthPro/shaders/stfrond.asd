///////////////////////////////////////////////////////////////////////////////
// Shader Name: stfrond
// Copyright 2007 Google Inc. All Rights Reserved.
// Reworked into GE/Alchemy by: dfalcantara@google.com (Dan Alcantara)
///////////////////////////////////////////////////////////////////////////////

#include <stcommonobjects.ini>

///////////////////////////////////////////////////////////////////////////////
[INTERFACE]
section  = stfrond

[stfrond]
attrs=

[CONFIGURATION]
fileName       = stfrond.cfg
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
_textDefinition.file  = stfrond.arbvp1
_entryPoint.value     = vmain
_stateList.value      = batch_model_view_proj_VS,   \
                        batch_camera_location_VS,   \
                        lightDirection_VS,          \
                        treePosAndScale_VS,         \
                        treeRotationVector_VS,      \
                        lod_profile_VS,             \
                        materialDiffuse_VS

///////////////////////////////////////////////////////////////////////////////
// PIXEL SHADER
///////////////////////////////////////////////////////////////////////////////
[bindPixelShader]
type                = igPixelShaderBindAttr
fields              = _pixelShader
_pixelShader.value  = pixelShader

[pixelShader]
type                  = igPixelShaderAttr
fields                = _textDefinition,_entryPoint,_stateList,_samplerList
_textDefinition.file  = stfrond.arbfp1
_entryPoint.value     = pmain
_stateList.value      = materialAmbient_PS, \
                        materialDiffuse_PS
_samplerList.value    = diffuseMap_PS,  \
                        normalMap_PS

