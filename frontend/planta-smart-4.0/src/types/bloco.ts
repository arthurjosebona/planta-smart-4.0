export type CorBloco = 'preto' | 'vermelho' | 'azul'
export type CorLamina = 'vermelho' | 'azul' | 'amarelo' | 'verde' | 'preto' | 'branco'
export type CorTampa = 'preto' | 'vermelho' | 'azul'
export type Padrao = 'casa' | 'estrela' | 'navio'
export type Face = 'frente' | 'esquerda' | 'direita'

export interface ConfigLamina {
  cor: CorLamina | null
  padrao: Padrao | null
}

export interface ConfigBloco {
  cor: CorBloco
  laminas: Record<Face, ConfigLamina>
}

export interface ConfiguradorState {
  numBlocos: 1 | 2 | 3
  corTampa: CorTampa
  blocos: [ConfigBloco, ConfigBloco, ConfigBloco] // sempre 3, numBlocos decide quantos renderizar
}
