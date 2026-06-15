import React from 'react';
import styles from './Smart40Bancada.module.css';
import { BancadaStatus, ModuleStatus } from './types';

import bancadaImg from '../../../../assets/bancada/Smart40.png';

import estOff   from '../../../../assets/bancada/Smart40-Est_off.png';
import estPause from '../../../../assets/bancada/Smart40-Est_pause.png';
import estOn    from '../../../../assets/bancada/Smart40-Est_on.png';

import proOff   from '../../../../assets/bancada/Smart40-Pro_off.png';
import proPause from '../../../../assets/bancada/Smart40-Pro_pause.png';
import proOn    from '../../../../assets/bancada/Smart40-Pro_on.png';

import monOff   from '../../../../assets/bancada/Smart40-Mon_off.png';
import monPause from '../../../../assets/bancada/Smart40-Mon_pause.png';
import monOn    from '../../../../assets/bancada/Smart40-Mon_on.png';

import expOff   from '../../../../assets/bancada/Smart40-Exp_off.png';
import expPause from '../../../../assets/bancada/Smart40-Exp_pause.png';
import expOn    from '../../../../assets/bancada/Smart40-Exp_on.png';

type OverlayMap = Record<ModuleStatus, string>;

const overlays: Record<keyof BancadaStatus, OverlayMap> = {
  estoque:  { off: estOff,  pause: estPause,  on: estOn  },
  processo: { off: proOff,  pause: proPause,  on: proOn  },
  montagem: { off: monOff,  pause: monPause,  on: monOn  },
  expedicao:{ off: expOff,  pause: expPause,  on: expOn  },
};

interface Props {
  status: BancadaStatus;
}

export function Smart40Bancada({ status }: Props) {
  return (
    <div className={styles.wrapper} aria-label="Bancada Smart 4.0">
      <img
        src={bancadaImg}
        alt="Bancada Smart 4.0"
        className={styles.base}
        draggable={false}
      />

      {(Object.keys(overlays) as Array<keyof BancadaStatus>).map((module) => {
        const src = overlays[module][status[module]];
        return (
          <img
            key={module}
            src={src}
            alt=""
            aria-hidden="true"
            className={styles.overlay}
            draggable={false}
          />
        );
      })}
    </div>
  );
}