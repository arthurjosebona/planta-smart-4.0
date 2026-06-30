import { useEffect, useState } from 'react';

export function useTempoDecorrido(registroEntrada: string | Date | null | undefined): string {
  const [tempoFormatado, setTempoFormatado] = useState('--:--:--');

  useEffect(() => {
    if (!registroEntrada) {
      setTempoFormatado('--:--:--');
      return;
    }

    const inicio = new Date(registroEntrada).getTime();

    function atualizar() {
      const diffMs = Date.now() - inicio;
      setTempoFormatado(formatarDuracao(diffMs));
    }

    atualizar(); // calcula já na primeira render, sem esperar 1s
    const intervalId = setInterval(atualizar, 1000);

    return () => clearInterval(intervalId);
  }, [registroEntrada]);

  return tempoFormatado;
}

function formatarDuracao(ms: number): string {
  const totalSegundos = Math.max(0, Math.floor(ms / 1000));
  const horas = Math.floor(totalSegundos / 3600);
  const minutos = Math.floor((totalSegundos % 3600) / 60);
  const segundos = totalSegundos % 60;

  const pad = (n: number) => String(n).padStart(2, '0');
  return `${pad(horas)}:${pad(minutos)}:${pad(segundos)}`;
}