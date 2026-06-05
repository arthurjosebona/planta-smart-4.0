import { LaminaCreateRequestDTO } from "@dtos/request/LaminaCreateRequestDTO";
import { Lamina } from "@entities/Lamina";
import { CorLaminaToInt } from "@enums/CorLamina";
import { PadraoLaminaToInt } from "@enums/PadraoLamina";
import { PosicaoLaminaToInt } from "@enums/PosicaoLamina";

export const LaminaMapper = {
    mapToCreateRequestDTO(entity: Lamina): LaminaCreateRequestDTO {
        return {
            cor: CorLaminaToInt[entity.cor],
            padrao: PadraoLaminaToInt[entity.padrao],
            posicao: PosicaoLaminaToInt[entity.posicao]
        }
    },

    mapLaminasToCreateRequestsDTO(entities: Lamina[]): LaminaCreateRequestDTO[] {
        const requests: LaminaCreateRequestDTO[] = [];

        entities.forEach(entity => {
            requests.push(this.mapToCreateRequestDTO(entity));
        });

        return requests;
    }
}