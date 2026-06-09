import { HttpClient } from '@http/HttpClient';
import { PedidoService } from '@service/PedidoService';
import { PedidoRepository } from '@repositoriesImp/PedidoRepository';
import { ExpedicaoRepository } from '@repositoriesImp/ExpedicaoRepository';
import { EstoqueRepository } from '@repositoriesImp/EstoqueRepository';
import { ExpedicaoService } from '@service/ExpedicaoService';
import { EstoqueService } from '@service/EstoqueService';
import { CacheService } from '@service/CacheService';

export const httpClient = new HttpClient();
export const pedidoRepository = new PedidoRepository(httpClient);
export const expedicaoRepository = new ExpedicaoRepository(httpClient);
export const estoqueRepository = new EstoqueRepository(httpClient);
export const pedidoService = new PedidoService(pedidoRepository);
export const expedicaoService = new ExpedicaoService(expedicaoRepository);
export const estoqueService = new EstoqueService(estoqueRepository);
export const cacheService = new CacheService();