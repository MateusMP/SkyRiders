#version 150

struct LightProperties
{
	vec4 position;
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
};
uniform	LightProperties u_light;

// TRANSFORMATION
uniform mat4 u_modelMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
//

// WIND
uniform vec3 u_wind; // Wind direction
uniform int u_time;
//

in vec3 a_position;
in vec3 a_normal;
in vec3 a_tangent;
in vec2 a_texcoord;

out vec2 v_texcoord;
out vec3 v_normal;
out vec3 v_tangent;
out vec3 toLightVector;
out vec3 toCameraVector;

vec3 calculate_wind(vec3 pos){
    float timeSec = u_time;
    float sinTimeWind = sin(timeSec/120);
    float sinTimeSec = sin(timeSec/30);

    vec3 u_pivot = vec3(0,0,0);
    float factor = max(0.0, distance(u_pivot, pos)-14)*0.05;
    float factorY = max(0.0, distance(u_pivot, pos)-20)*0.05;

    vec3 wind = u_wind * sinTimeWind;
    vec3 windSplash = vec3(-sinTimeSec,sinTimeSec,sinTimeSec);

    return wind*factor + windSplash*factorY;
}

void main(void)
{
    // Apply wind
    vec3 wind = calculate_wind(a_position);
    vec3 vpos = a_position + wind;
    
    vec4 worldPosition = u_modelMatrix * vec4(vpos, 1.0);
    gl_Position = u_projectionMatrix * u_viewMatrix * worldPosition;
    v_texcoord = a_texcoord;

    // Surface normal
    v_normal = (u_modelMatrix * vec4(a_normal, 0.0)).xyz;

    // Normal map tangents
    v_tangent = (u_modelMatrix * vec4(a_tangent, 0.0)).xyz;

    // To light vector
    toLightVector = (u_light.position - worldPosition).xyz;
    toCameraVector = (inverse(u_viewMatrix)*vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}
