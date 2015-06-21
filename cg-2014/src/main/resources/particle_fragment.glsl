#version 130
#extension GL_ARB_gpu_shader5 : enable

struct MaterialProperties
{
	vec4 ambientColor;
	vec4 diffuseColor;
};

uniform	MaterialProperties u_material;
uniform mat4 u_viewMatrix;
uniform mat4 u_modelMatrix;

uniform sampler2D u_texture;

in vec2 v_texcoord;

out vec4 fragColor;

void main(void)
{
    vec4 finalAmbientColor = u_material.ambientColor;

    vec4 texColor = texture(u_texture, v_texcoord);
    if (texColor.a < 0.01)
        discard;

    fragColor = (u_material.diffuseColor + finalAmbientColor)*texColor;
}
