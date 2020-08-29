#version 150

uniform mat4 u_viewMatrix;
uniform mat4 u_modelMatrix;

uniform sampler2D u_texture;

in vec2 v_texcoord;
out vec4 fragColor;

void main(void)
{	
    fragColor = texture(u_texture, v_texcoord);
}
