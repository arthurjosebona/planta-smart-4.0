import React from 'react';
import styles from './estacoesSection.module.css';
import { MonitorModel } from '@pages/Monitor/MonitorModel';
import type { ModuleStatus } from './types';

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

// Pipeline status (0/1/2): derivado do statusXxx do AppStateConfig via SSE
const pipelineOverlays: Record<string, [string, string, string]> = {
  estoque:   [estSt0, estSt1, estSt2],
  processo:  [proSt0, proSt1, proSt2],
  montagem:  [monSt0, monSt1, monSt2],
  expedicao: [expSt0, expSt1, expSt2],
};

// Conexão status: derivado do ping + status CLP via SSE
const connectionOverlays: Record<string, Record<ModuleStatus, string>> = {
  estoque:   { off: estOff, pause: estPause, on: estOn },
  processo:  { off: proOff, pause: proPause, on: proOn },
  montagem:  { off: monOff, pause: monPause, on: monOn },
  expedicao: { off: expOff, pause: expPause, on: expOn },
};

type StationKey = 'estoque' | 'processo' | 'montagem' | 'expedicao';

interface EstacoesProps {
  status: MonitorModel;
  moduleStatus: Record<StationKey, ModuleStatus>;
}

export function Estacoes({ status, moduleStatus }: EstacoesProps) {
  const st = status.estoque;

  // Status de produção (0/1/2) de cada estação, calculado no backend a partir das
  // flags do CLP (startOP -> 1, finishOP -> 2) e trafegado no stream da estação ESTOQUE.
  const pipelineValues: Record<StationKey, number | undefined> = {
    estoque:   st?.statusEstoque,
    processo:  st?.statusProcesso,
    montagem:  st?.statusMontagem,
    expedicao: st?.statusExpedicao,
  };

  // Flag "OPInCourse": indica que ainda há uma ordem de produção em andamento na bancada.
  // Enquanto verdadeira, uma estação que já finalizou sua etapa permanece em St2.
  // pedidoEmCurso sobe ao iniciar a ordem; statusProducao vira 1 quando a ordem é
  // concluída na expedição (e volta a 0 no início da próxima), encerrando o St2.
  const opInCourse = (st?.pedidoEmCurso ?? false) && (st?.statusProducao ?? 0) === 0;

  // Flag de ocupação (estação em uso no momento) de cada estação.
  const ocupadoValues: Record<StationKey, boolean> = {
    estoque:   status.estoque?.ocupado ?? false,
    processo:  status.processo?.ocupado ?? false,
    montagem:  status.montagem?.ocupado ?? false,
    expedicao: status.expedicao?.ocupado ?? false,
  };

  // Decide qual overlay de pipeline exibir (0/1/2) ou nenhum, com base nas flags do CLP:
  //  - desconectada            -> nenhum overlay de pipeline (só o de conexão "off");
  //  - ocupada (em uso)        -> St1;
  //  - finalizou (status 2) e ordem ainda em curso (opInCourse) -> St2;
  //  - conectada e em aguardo  -> St0.
  function pipelineIndex(station: StationKey): number | null {
    if (moduleStatus[station] === 'off') return null;
    if (ocupadoValues[station]) return 1;
    if (pipelineValues[station] === 2 && opInCourse) return 2;
    return 0;
  }

  const stations: StationKey[] = ['estoque', 'processo', 'montagem', 'expedicao'];

  return (
    <div className={styles.wrapper} aria-label="Bancada Smart 4.0">
      <img
        src={bancadaImg}
        alt="Bancada Smart 4.0"
        className={styles.base}
        draggable={false}
      />

      {/* Camada 1: status de produção (pipeline 0/1/2) */}
      {stations.map((station) => {
        const value = pipelineIndex(station);
        if (value === null) return null;
        const overlays = pipelineOverlays[station];
        return (
          <img
            key={`pipeline-${station}`}
            src={overlays[value]}
            className={styles.overlay}
            alt={`${station} pipeline ${value}`}
            draggable={false}
          />
        );
      })}

      {/* Camada 2: status de conexão (off/pause/on via ping + SSE) */}
      {stations.map((station) => {
        const connStatus = moduleStatus[station];
        const src = connectionOverlays[station][connStatus];
        return (
          <img
            key={`conn-${station}`}
            src={src}
            className={styles.overlay}
            alt={`${station} ${connStatus}`}
            draggable={false}
          />
        );
      })}
    </div>
  );
}
