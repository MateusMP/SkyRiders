#version 150

uniform mat4 u_modelMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;

in vec3 a_position;
in vec2 a_texcoord;

out vec2 v_texcoord;

void main(void)
{ 
    vec4 worldPosition = u_modelMatrix * vec4(a_position, 1.0);
    gl_Position = u_projectionMatrix * u_viewMatrix * worldPosition;
    v_texcoord = a_texcoord;
}