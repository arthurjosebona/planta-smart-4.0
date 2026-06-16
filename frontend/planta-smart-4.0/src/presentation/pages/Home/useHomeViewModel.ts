import { useState } from 'react';
import { StoreModel, StoreModelInitial } from '@pages/Home/HomeModel';
// import { connectionService } from '@config/diContainer';

export function useHomeViewModel() {
  const [model, setModel] = useState<StoreModel>(StoreModelInitial);

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
      // await connectionService.conectar({ modulos: model.modulos });
      await new Promise((res) => setTimeout(res, 1200)); // placeholder
      setModel((s) => ({
        ...s,
        loading: false,
        conectado: true,
        sucesso: 'Conexão estabelecida com sucesso.',
      }));
    } catch {
      setModel((s) => ({
        ...s,
        loading: false,
        conectado: false,
        erro: 'Falha ao conectar. Verifique a rede e tente novamente.',
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
    handleFaixaChange,
    confirmarFaixa,
    conectar,
    dismissErro,
    dismissSucesso,
  };
}