import React from 'react';
import styles from './estacoesSection.module.css';
import { MonitorModel } from '@pages/Monitor/MonitorModel';

import bancadaImg from '@assets/bancada/Smart40.png';

// Overlays de status de produção (pipeline: 0=aguardando, 1=em execução, 2=concluído)
import estSt0 from '@assets/bancada/Smart40_Estoque_0.png';
import estSt1 from '@assets/bancada/Smart40_Estoque_1.png';
import estSt2 from '@assets/bancada/Smart40_Estoque_2.png';

import proSt0 from '@assets/bancada/Smart40_Processo_0.png';
import proSt1 from '@assets/bancada/Smart40_Processo_1.png';
import proSt2 from '@assets/bancada/Smart40_Processo_2.png';

import monSt0 from '@assets/bancada/Smart40_Montagem_0.png';
import monSt1 from '@assets/bancada/Smart40_Montagem_1.png';
import monSt2 from '@assets/bancada/Smart40_Montagem_2.png';

import expSt0 from '@assets/bancada/Smart40_Expedicao_0.png';
import expSt1 from '@assets/bancada/Smart40_Expedicao_1.png';
import expSt2 from '@assets/bancada/Smart40_Expedicao_2.png';

// Overlays de conexão (off=desligado, pause=aguardando, on=ativo)
import estOff   from '@assets/bancada/Smart40-Est_off.png';
import estPause from '@assets/bancada/Smart40-Est_pause.png';
import estOn    from '@assets/bancada/Smart40-Est_on.png';

import proOff   from '@assets/bancada/Smart40-Pro_off.png';
import proPause from '@assets/bancada/Smart40-Pro_pause.png';
import proOn    from '@assets/bancada/Smart40-Pro_on.png';

import monOff   from '@assets/bancada/Smart40-Mon_off.png';
import monPause from '@assets/bancada/Smart40-Mon_pause.png';
import monOn    from '@assets/bancada/Smart40-Mon_on.png';

import expOff   from '@assets/bancada/Smart40-Exp_off.png';
import expPause from '@assets/bancada/Smart40-Exp_pause.png';
import expOn    from '@assets/bancada/Smart40-Exp_on.png';
import { Estacao } from '@enums/Estacao';
import { EstacaoStatusModule } from '@enums/EstacaoStatusModule';
import { EstacaoStatusPipe } from '@enums/EstacaoStatusPipe';

const connectionPipeOverlays: Record<Estacao, Record<EstacaoStatusPipe, string>> = {
  [Estacao.Estoque]:   { [EstacaoStatusPipe.Desligado]: estOff, [EstacaoStatusPipe.Ocupado]: estPause, [EstacaoStatusPipe.Ligado]: estOn },
  [Estacao.Processo]:  { [EstacaoStatusPipe.Desligado]: proOff, [EstacaoStatusPipe.Ocupado]: proPause, [EstacaoStatusPipe.Ligado]: proOn },
  [Estacao.Montagem]:  { [EstacaoStatusPipe.Desligado]: monOff, [EstacaoStatusPipe.Ocupado]: monPause, [EstacaoStatusPipe.Ligado]: monOn },
  [Estacao.Expedicao]: { [EstacaoStatusPipe.Desligado]: expOff, [EstacaoStatusPipe.Ocupado]: expPause, [EstacaoStatusPipe.Ligado]: expOn },
};

const connectionOverlays: Record<Estacao, Record<EstacaoStatusModule, string | null>> = {
  [Estacao.Estoque]: {[EstacaoStatusModule.Desligado]: null, [EstacaoStatusModule.Aguardando]: estSt0, [EstacaoStatusModule.Ocupado]: estSt1, [EstacaoStatusModule.Finalizado]: estSt2},
  [Estacao.Processo]: {[EstacaoStatusModule.Desligado]: null, [EstacaoStatusModule.Aguardando]: proSt0, [EstacaoStatusModule.Ocupado]: proSt1, [EstacaoStatusModule.Finalizado]: proSt2},
  [Estacao.Montagem]: {[EstacaoStatusModule.Desligado]: null, [EstacaoStatusModule.Aguardando]: monSt0, [EstacaoStatusModule.Ocupado]: monSt1, [EstacaoStatusModule.Finalizado]: monSt2},
  [Estacao.Expedicao]: {[EstacaoStatusModule.Desligado]: null, [EstacaoStatusModule.Aguardando]: expSt0, [EstacaoStatusModule.Ocupado]: expSt1, [EstacaoStatusModule.Finalizado]: expSt2}

}



interface EstacoesProps {
  status: MonitorModel;
  statusEstacoes: Record<Estacao, EstacaoStatusModule>;
  statusPipelines: Record<Estacao, EstacaoStatusPipe>;
}

const stations: Estacao[] = [Estacao.Estoque, Estacao.Processo, Estacao.Montagem, Estacao.Expedicao];

export function Estacoes({ status, statusEstacoes, statusPipelines }: EstacoesProps) {
 
  return (
    <div className={styles.wrapper} aria-label="Bancada Smart 4.0">
      <img
        src={bancadaImg}
        alt="Bancada Smart 4.0"
        className={styles.base}
        draggable={false}
      />

      {/* Camada 1: status de ping */}
      {stations.map((station) => {
        const pipeStatusValue = statusPipelines[station]
        const src = connectionPipeOverlays[station][pipeStatusValue];
        return (
          <img
            key={`pipeline-${station}`}
            src={src}
            className={styles.overlay}
            alt={`${station} pipeline ${pipeStatusValue}`}
            draggable={false}
          />
        );
      })}

      {/* Camada 2: status de produção */}
      {stations.map((station) => {
        const moduleStatusValue = statusEstacoes[station];
        const src = connectionOverlays[station][moduleStatusValue];

        if (!src) return null; // Desligado -> sem overlay
        return (
          <img
            key={`conn-${station}`}
            src={src}
            className={styles.overlay}
            alt={`${station} ${moduleStatusValue}`}
            draggable={false}
          />
        );
      })}
    </div>
  );
}
