package com.smart.appsa.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.mapper.ExpedicaoMapper;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.service.EstoqueService;
import com.smart.appsa.service.ExpedicaoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final EstoqueService estoqueService;
    private final ExpedicaoService expedicaoService;

    // home

    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/pedidos/novo";
    }

    // pedidos 

    @GetMapping("/pedidos/novo")
    public String formCriar(Model model) {
        model.addAttribute("tiposPedido",    TipoPedido.values());
        model.addAttribute("coresTampa",     CorTampa.values());
        model.addAttribute("coresBlocos",    CorBloco.values());
        model.addAttribute("coresLaminas",   CorLamina.values());
        model.addAttribute("padroesLamina",  PadraoLamina.values());
        model.addAttribute("posicoesLamina", PosicaoLamina.values());
        return "pedido";
    }

    // dashboard 

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        popularDashboard(model, false);
        return "dashboard";
    }

    @GetMapping("/dashboard/editar")
    public String dashboardEditar(Model model) {
        popularDashboard(model, true);
        return "dashboard";
    }

    @PostMapping("/dashboard/editar")
    public String salvarEstoque(@RequestParam List<Long> ids, @RequestParam List<CorEstoque> cores) {
        List<Estoque> estoque = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            Estoque e = new Estoque();
            e.setId(ids.get(i));
            e.setCorEstoque(cores.get(i));
            estoque.add(e);
        }
        estoqueService.updateAllEstoque(estoque);
        return "redirect:/dashboard";
    }


    private void popularDashboard(Model model, boolean editMode) {
        List<Estoque>   estoque   = estoqueService.findAll().stream().map(e -> EstoqueMapper.mapEntityByResponseDTO(e)).toList();
        List<Expedicao> expedicao = expedicaoService.findAll().stream().map(e -> ExpedicaoMapper.mapEntityByResponseDTO(e)).toList();

        model.addAttribute("estoque",   estoque);
        model.addAttribute("expedicao", expedicao);
        model.addAttribute("editMode",  editMode);
    }
}