#version 130

struct LightProperties
{
	vec4 position;
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
};

struct MaterialProperties
{
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
	float specularExponent;
};

uniform	LightProperties u_light;
uniform	MaterialProperties u_material;
uniform mat4 u_viewMatrix;
uniform mat4 u_modelMatrix;

uniform sampler2D u_texture;
uniform sampler2D u_dudv;
uniform bool u_is_texture;

uniform float move_factor;

in vec2 v_texcoord;
in vec3 v_normal;		// Surface normal
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 fragColor;

const float waveStrength = 0.06;
void main(void)
{
	vec4 finalAmbientColor = u_light.ambientColor * u_material.ambientColor;

	vec3 unitLightVector = normalize(toLightVector);
	vec3 unitToCamera = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;

        vec2 distortion1 = (texture(u_dudv, v_texcoord+move_factor).rg * 2.0 -1.0)*waveStrength;
        vec2 distortion2 = (texture(u_dudv, v_texcoord+move_factor/2).rg * 2.0 -1.0)*(waveStrength/2);
	
        vec2 result_coord = v_texcoord + distortion1;
        result_coord = clamp(result_coord, 0.001, 0.999);
        
        vec2 result_coord2 = v_texcoord + distortion2;
        result_coord2 = clamp(result_coord, 0.001, 0.999);
        

	vec4 texColor = vec4(1.0, 1.0, 1.0, 1.0);
	if (u_is_texture) {
            texColor = texture(u_texture, result_coord);
	}
	texColor.a = 1.0;
        
        vec3 unitNormal = normalize( texture(u_dudv, result_coord2).rgb );

	float nDotl = dot(unitNormal, unitLightVector);
	float brightness = max(nDotl, 0.0);
	vec4 diffuse = brightness * u_light.diffuseColor * u_material.diffuseColor;
	
	
	vec3 reflectLightDirection = reflect(lightDirection, unitNormal);
	float specularFactor = dot(reflectLightDirection, unitToCamera);
	specularFactor = max(specularFactor, 0.0);
	float specularIntensity = pow(specularFactor, u_material.specularExponent);
	
	vec4 finalSpecular = u_material.specularColor * u_light.specularColor * specularIntensity;
	
	fragColor = (diffuse+finalAmbientColor)*texColor + finalSpecular;
}
