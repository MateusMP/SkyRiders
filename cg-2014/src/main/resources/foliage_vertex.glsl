#version 150
#extension GL_ARB_gpu_shader5 : enable

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

vec3 calculate_wind(vec3 pos, float eA, float eB){
    vec3 u_pivot = vec3(0,0,0);
    float timeSec = u_time + eA/8;
    float distance = distance(u_pivot, pos);
	float cos_time = cos(timeSec/3 + eB);
    float sinTimeWind = clamp(pow(sin(cos(timeSec/120)*2),8),0,1)+0.15;
    float sinTimeSec = sinTimeWind*(cos_time/5+1);

    float factor = max(0.0, distance-14)*0.1;
    float factorY = max(0.0, distance-20)*0.01;

    vec3 wind = u_wind * sinTimeWind;
    vec3 windSplash = ((vec3(-cos_time,cos_time,cos_time)+wind)*sinTimeSec)/2;

    return wind*factor + windSplash*factorY;
}

void main(void)
{
    mat4 modelTI = transpose(inverse(u_modelMatrix));
 
    vec4 worldPosition = u_modelMatrix * vec4(a_position, 1.0);

    // Apply wind
    vec3 wind = calculate_wind(a_position, worldPosition.x, worldPosition.z);
    worldPosition = worldPosition + vec4(wind,0);

    gl_Position = u_projectionMatrix * u_viewMatrix * worldPosition;
    v_texcoord = a_texcoord;

    // Surface normal
    v_normal = (modelTI * vec4(a_normal, 0.0)).xyz;

    // Normal map tangents
    v_tangent = (u_modelMatrix * vec4(a_tangent, 0.0)).xyz;

    // To light vector
    toLightVector = (u_light.position - worldPosition).xyz;
    toCameraVector = (inverse(u_viewMatrix)*vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}
