#version 130
#extension GL_ARB_gpu_shader5 : enable

uniform mat4 u_modelMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;

in vec3 a_position;
in vec2 a_texcoord;

out vec2 v_texcoord;

void main(void)
{


    mat4 VP = u_projectionMatrix * u_viewMatrix;

    vec3 pos = vec3(u_modelMatrix[3][0], u_modelMatrix[3][1], u_modelMatrix[3][2]);
    vec3 vertexPosition_worldspace = pos;

    // Get the screen-space position of the particle's center
    gl_Position = VP * vec4(vertexPosition_worldspace, 1.0f); 
    
    // Here we have to do the perspective division ourselves.
    gl_Position /= gl_Position.w;

    // Move the vertex in directly screen space. No need for CameraUp/Right_worlspace here.
    gl_Position.xy += a_position.xy * vec2(0.1, 0.1);


/*
        vec4 BillboardPos_worldspace = u_modelMatrix * vec4(a_position, 1); // (x,y,z, 1.0f);
        vec4 BillboardPos_screenspace = u_projectionMatrix * u_viewMatrix * BillboardPos_worldspace;
        BillboardPos_screenspace /= BillboardPos_screenspace.w;
        gl_Position = BillboardPos_screenspace;
*/

/*
		vec4 worldPos = u_modelMatrix * vec4(a_position,1);
        mat4 MV = inverse(u_viewMatrix * u_modelMatrix);

        vec4 wpos = vec4(worldPos.xyz, 1.0);
        vec4 epos = MV * wpos;
        // epos.xy += a_position.xy * worldPos.w; 
        gl_Position = gl_ProjectionMatrix * epos;
        //gl_TexCoord[0] = gl_Vertex*0.5 + vec4(0.5);
        //gl_FrontColor = gl_Color;
*/

/*
mat4 MV = u_viewMatrix * u_modelMatrix;
mat4 MVP = u_projectionMatrix * u_viewMatrix;
mar4 bufferMatrix = u_modelMatrix;

gl_Position = (MVP * bufferMatrix * vec4(0.0, 0.0, 0.0, 1.0)) + (MV * vec4(
    a_position.x * 1.0 * bufferMatrix[0][0], 
    a_position.y * 1.0 * bufferMatrix[1][1], 
    a_position.z * 1.0 * bufferMatrix[2][2], 
    0.0)
*/

/*
    vec3 CameraRight_worldspace = vec3(u_viewMatrix[0][0], u_viewMatrix[1][0], u_viewMatrix[2][0]);
    vec3 CameraUp_worldspace = vec3(u_viewMatrix[0][1], u_viewMatrix[1][1], u_viewMatrix[2][1]);

    vec3 vertexPosition_worldspace = 
        vec3(u_modelMatrix[3][0], u_modelMatrix[3][1], u_modelMatrix[3][2])
        + CameraRight_worldspace * a_position.x * u_modelMatrix[0][0]
        + CameraUp_worldspace * a_position.y * u_modelMatrix[0][1];
    
    gl_Position = vec4(vertexPosition_worldspace, 1);
*/

    v_texcoord = a_texcoord;
}