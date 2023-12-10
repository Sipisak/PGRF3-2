#version 330 core
in vec3 inPosition;
in vec3 inNormal;

out vec3 Normal;
out vec3 Position;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;

void main()
{
    Normal = mat3(transpose(inverse(model))) * inNormal;
    Position = vec3(uModel * vec4(inPosition, 1.0));
    gl_Position = uProj * uView * vec4(Position, 1.0);
}