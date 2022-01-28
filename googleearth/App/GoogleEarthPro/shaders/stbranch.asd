///////////////////////////////////////////////////////////////////////////////
// Shader Name: stbranch
// Copyright 2007 Google Inc. All Rights Reserved.
// Reworked into GE/Alchemy by: dfalcantara@google.com (Dan Alcantara)
///////////////////////////////////////////////////////////////////////////////

#include <stcommonobjects.ini>

///////////////////////////////////////////////////////////////////////////////
[INTERFACE]
section  = stbranch

[stbranch]
attrs=

[CONFIGURATION]
fileName       = stbranch.cfg
implementation = hardware_shader_implementation


///////////////////////////////////////////////////////////////////////////////
//  PASS DECLARATIONS
//    Most of the attributes are contained in stcommonobjects.ini.
///////////////////////////////////////////////////////////////////////////////
[single_pass]
attrs = enableVertexShaderATTR,\
        bindVertexShader,\
        enablePixelShaderATTR,\
        bindPixelShader, \
        cullATTR


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
_textDefinition.file  = stbranch.arbvp1
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
_textDefinition.file  = stbranch.arbfp1
_entryPoint.value     = pmain
_stateList.value      = materialAmbient_PS, \
                        materialDiffuse_PS
_samplerList.value    = diffuseMap_PS,  \
                        normalMap_PS

