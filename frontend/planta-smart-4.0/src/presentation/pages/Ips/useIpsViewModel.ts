import { useEffect, useState } from 'react';
import { StoreModel, StoreModelInitial } from '@pages/Ips/IpsModel';
import { conexaoService } from '@config/diContainer';
import { useStatusContext } from '@contexts/StatusContext';


export function useIpsViewModel() {
  const [model, setModel] = useState<StoreModel>(StoreModelInitial);
  const { conectado } = useStatusContext();

  // Sincroniza o estado inicial do readOnly com o backend ao montar.
  useEffect(() => {
    conexaoService
      .getReadOnly()
      .then((readOnly) => setModel((s) => ({ ...s, readOnly })))
      .catch(() => {
        /* mantém o padrão (false) caso o backend esteja indisponível */
      });
  }, []);

  async function toggleReadOnly(value: boolean) {
    const anterior = model.readOnly;
    // Atualização otimista — reverte se a chamada falhar.
    setModel((s) => ({ ...s, readOnly: value, erro: null }));
    try {
      await conexaoService.setReadOnly(value);
    } catch {
      setModel((s) => ({
        ...s,
        readOnly: anterior,
        erro: 'Não foi possível alterar o modo somente-leitura.',
      }));
    }
  }

  function handleFaixaChange(value: string) {
    const octets = value.split('.');
    const novosModulos = model.modulos.map((modulo) => {
      const ipOctets = modulo.ip.split('.');
      const novoIp = [
        octets[0] ?? ipOctets[0],
        octets[1] ?? ipOctets[1],
        octets[2] ?? ipOctets[2],
        ipOctets[3],
      ].join('.');
      return { ...modulo, ip: novoIp };
    });
    setModel((s) => ({ ...s, faixa: value, modulos: novosModulos }));
  }

  function confirmarFaixa() {
    const isValida = /^\d{1,3}(\.\d{1,3}){2,3}$/.test(model.faixa.trim());
    if (!isValida) {
      setModel((s) => ({
        ...s,
        erro: 'Faixa inválida. Use o formato: 10.74.241.0',
        sucesso: null,
      }));
      return;
    }
    setModel((s) => ({ ...s, erro: null, sucesso: 'Faixa aplicada com sucesso.' }));
  }

  async function conectar() {
    setModel((s) => ({ ...s, loading: true, erro: null, sucesso: null }));
    try {
      // 1) Aplica os IPs configurados nos CLPs.
      await conexaoService.conectar(model.modulos);
      // 2) Inicia as leituras dos CLPs, que passam a alimentar os streams da bancada.
      //    O resultado informa quais estações realmente conectaram.
      const resultado = await conexaoService.iniciarLeituras(model.modulos);

      if (resultado.todasConectadas) {
        setModel((s) => ({
          ...s,
          loading: false,
          erro: null,
          sucesso: 'Conexão estabelecida e streams da bancada iniciados.',
        }));
      } else {
        const falhas = Object.entries(resultado.resultados)
          .filter(([, ok]) => !ok)
          .map(([nome]) => nome);
        setModel((s) => ({
          ...s,
          loading: false,
          sucesso: null,
          erro: `Falha ao conectar nas estações: ${falhas.join(', ')}. Verifique a rede e os IPs.`,
        }));
      }
    } catch {
      setModel((s) => ({
        ...s,
        loading: false,
        erro: 'Falha ao conectar. Verifique a rede e tente novamente.',
      }));
    }
  }

  async function desconectar() {
    setModel((s) => ({ ...s, loading: true, erro: null, sucesso: null }));
    try {
      await conexaoService.desconectar();
      setModel((s) => ({ ...s, loading: false, sucesso: 'Desconectado com sucesso.' }));
    } catch {
      setModel((s) => ({
        ...s,
        loading: false,
        erro: 'Falha ao desconectar. Tente novamente.',
      }));
    }
  }

  function dismissErro() {
    setModel((s) => ({ ...s, erro: null }));
  }

  function dismissSucesso() {
    setModel((s) => ({ ...s, sucesso: null }));
  }

  return {
    model,
    conectado,
    handleFaixaChange,
    confirmarFaixa,
    conectar,
    desconectar,
    toggleReadOnly,
    dismissErro,
    dismissSucesso,
  };
}