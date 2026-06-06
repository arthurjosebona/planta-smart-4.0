import { HttpClient } from "@http/HttpClient"
import { PedidoService } from "@service/PedidoService";
import { PedidoRepository } from "@repositoriesImp/PedidoRepository"

export const httpClient = new HttpClient();
export const pedidoRepository = new PedidoRepository(httpClient);
export const pedidoService = new PedidoService(pedidoRepository);