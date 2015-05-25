#version 130

struct MaterialProperties
{
    vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
    float specularExponent;
};

uniform	MaterialProperties u_material;
uniform mat4 u_viewMatrix;
uniform mat4 u_modelMatrix;

uniform sampler2D u_texture;
uniform bool u_is_texture;


in vec2 v_texcoord;
out vec4 fragColor;

void main(void)
{	
    if(u_is_texture){
        vec4 texColor = texture(u_texture, v_texcoord);
	
        fragColor = texColor;
    }
    else{
        fragColor =  u_material.ambientColor + u_material.diffuseColor + 
            u_material.specularColor + u_material.specularExponent;
    }
}
