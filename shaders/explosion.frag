#version 330

in vec3 color;
in vec2 TexCoord;
out vec4 outColor;

uniform sampler2D textureExplosion ;

void main(){
    vec4 explosionColor = texture(textureExplosion, TexCoord);
    outColor = vec4(color * explosionColor.rgb, 1.0);
}
