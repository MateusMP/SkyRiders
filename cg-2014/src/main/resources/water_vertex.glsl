#version 130

struct LightProperties
{
	vec4 position;
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
};
uniform	LightProperties u_light;

uniform mat4 u_modelMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;

uniform float move_factor;

in vec3 a_position;
in vec3 a_normal;
in vec2 a_texcoord;

out vec2 v_texcoord;
out vec3 v_normal;
out vec3 toLightVector;
out vec3 toCameraVector;


void main(void)
{ 
	vec4 worldPosition = u_modelMatrix*vec4(a_position, 1.0);
        worldPosition.y += sin(worldPosition.x/20+move_factor*10)*10;
        gl_Position = u_projectionMatrix * u_viewMatrix * worldPosition;
	v_texcoord = a_texcoord;

	// Surface normal
	v_normal = (transpose(inverse(u_modelMatrix)) * vec4(a_normal, 0.0)).xyz;

	// To light vector
	toLightVector = (u_light.position - worldPosition).xyz;
	
	toCameraVector = (inverse(u_viewMatrix)*vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}