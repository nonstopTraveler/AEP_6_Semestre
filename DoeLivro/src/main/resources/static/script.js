const $ = (id) => document.getElementById(id);
const estado = { usuarios: [], livros: [], doacoes: [] };
let mensagemTimer;

function mostrarMensagem(texto, tipo = "sucesso") {
    const elemento = $("mensagem");
    elemento.textContent = texto;
    elemento.className = `mensagem visivel ${tipo}`;
    clearTimeout(mensagemTimer);
    mensagemTimer = setTimeout(() => elemento.classList.remove("visivel"), 5500);
}

async function requisicao(url, opcoes = {}) {
    const resposta = await fetch(url, {
        ...opcoes,
        headers: { ...(opcoes.body ? { "Content-Type": "application/json" } : {}), ...(opcoes.headers || {}) }
    });
    if (!resposta.ok) {
        let detalhe = `Erro HTTP ${resposta.status}`;
        const corpo = await resposta.text();
        if (corpo) {
            try {
                const dados = JSON.parse(corpo);
                detalhe = dados.message || dados.detail || dados.mensagem || detalhe;
            } catch (_) {
                detalhe = corpo;
            }
        }
        throw new Error(detalhe);
    }
    if (resposta.status === 204) return null;
    const texto = await resposta.text();
    return texto ? JSON.parse(texto) : null;
}

function escapar(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caractere => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[caractere]));
}

function preencherSelect(id, itens, textoVazio, selecionado = "") {
    const select = $(id);
    select.replaceChildren(new Option(textoVazio, ""));
    itens.forEach(item => {
        const option = new Option(item.rotulo, item.id);
        option.selected = item.id === selecionado;
        select.add(option);
    });
}

async function carregarTudo() {
    try {
        const [usuarios, livros, doacoes] = await Promise.all([
            requisicao("/usuarios"), requisicao("/livros"), requisicao("/doacoes")
        ]);
        estado.usuarios = usuarios || [];
        estado.livros = livros || [];
        estado.doacoes = doacoes || [];
        renderizarUsuarios();
        renderizarLivros();
        renderizarDoacoes();
        atualizarSeletores();
    } catch (erro) {
        mostrarMensagem(`Não foi possível carregar os dados: ${erro.message}`, "erro");
    }
}

function atualizarSeletores() {
    const usuarios = estado.usuarios.map(u => ({ id: u.id, rotulo: `${u.nome} (${u.email})` }));
    const livrosDisponiveis = estado.livros.filter(l => l.disponivel).map(l => ({ id: l.id, rotulo: `${l.titulo} — ${l.autor}` }));
    preencherSelect("doadorLivro", usuarios, "Não associado");
    preencherSelect("doadorDoacao", usuarios, "Selecione um usuário");
    preencherSelect("livroDoacao", livrosDisponiveis, "Selecione um livro disponível");
}

$("livroDoacao").addEventListener("change", () => {
    const livroSelecionado = estado.livros.find(l => l.id === $("livroDoacao").value);
    if (livroSelecionado?.doadorId) {
        $("doadorDoacao").value = livroSelecionado.doadorId;
        $("doadorDoacao").disabled = true;
    } else {
        $("doadorDoacao").disabled = false;
        $("doadorDoacao").value = "";
    }
});

function renderizarUsuarios() {
    const lista = $("listaUsuarios");
    if (!estado.usuarios.length) { lista.innerHTML = '<p class="vazio">Nenhum usuário cadastrado.</p>'; return; }
    lista.innerHTML = estado.usuarios.map(u => `
        <article class="item">
            <h3>${escapar(u.nome)}</h3>
            <p>${escapar(u.email)}</p>
            <p>${escapar(u.telefone || "Telefone não informado")}</p>
            <p>${escapar([u.cidade, u.estado].filter(Boolean).join(" / ") || "Localização não informada")}</p>
            <div class="acoes">
                <button type="button" data-editar-usuario="${escapar(u.id)}">Editar</button>
                <button type="button" class="perigo" data-excluir-usuario="${escapar(u.id)}">Excluir</button>
            </div>
        </article>`).join("");
}

function renderizarLivros() {
    const lista = $("listaLivros");
    if (!estado.livros.length) { lista.innerHTML = '<p class="vazio">Nenhum livro cadastrado.</p>'; return; }
    lista.innerHTML = estado.livros.map(l => {
        const doador = estado.usuarios.find(u => u.id === l.doadorId);
        return `<article class="item">
            <h3>${escapar(l.titulo)}</h3>
            <p>Autor: ${escapar(l.autor)}</p>
            <p>Categoria: ${escapar(l.categoria)}</p>
            <p>Conservação: ${escapar(l.estadoConservacao)}</p>
            <p>Doador: ${escapar(doador?.nome || "Não associado")}</p>
            <p>Disponível: ${l.disponivel ? "Sim" : "Não"}</p>
            <div class="acoes">
                <button type="button" data-editar-livro="${escapar(l.id)}">Editar</button>
                <button type="button" class="perigo" data-excluir-livro="${escapar(l.id)}">Excluir</button>
            </div>
        </article>`;
    }).join("");
}

function renderizarDoacoes() {
    const lista = $("listaDoacoes");
    if (!estado.doacoes.length) { lista.innerHTML = '<p class="vazio">Nenhuma doação registrada.</p>'; return; }
    lista.innerHTML = estado.doacoes.map(d => {
        const livro = estado.livros.find(l => l.id === d.livroId);
        const doador = estado.usuarios.find(u => u.id === d.doadorId);
        const historico = (d.historico || []).map(h => `
            <p>${escapar(h.status)} · ${escapar(h.data ? new Date(h.data).toLocaleString("pt-BR") : "data não informada")}<br>${escapar(h.observacao || "")}</p>`).join("");
        return `<article class="item">
            <h3>${escapar(livro?.titulo || "Livro não localizado")}</h3>
            <p>Doador: ${escapar(doador?.nome || "Usuário não localizado")}</p>
            <p><span class="status ${escapar(d.status)}">${escapar(d.status)}</span></p>
            <details><summary>Histórico (${(d.historico || []).length})</summary>${historico || "<p>Sem histórico.</p>"}</details>
            ${["DOADO", "CANCELADO"].includes(d.status) ? "" : `
                <div class="atualizar-status">
                    <select aria-label="Novo status" id="status-${escapar(d.id)}">
                        <option value="DISPONIVEL">Disponível</option>
                        <option value="RESERVADO">Reservado</option>
                        <option value="DOADO">Doado</option>
                        <option value="CANCELADO">Cancelado</option>
                    </select>
                    <button type="button" data-status-doacao="${escapar(d.id)}">Atualizar</button>
                </div>`}
        </article>`;
    }).join("");
}

function enderecoDoFormulario() {
    const rua = $("rua").value.trim();
    const bairro = $("bairro").value.trim();
    const cidade = $("cidadeEndereco").value.trim();
    const estado = $("estadoEndereco").value.trim();
    const campos = [rua, bairro, cidade, estado];
    const preenchido = [rua, $("numero").value, $("complemento").value, bairro, cidade, estado, $("cep").value].some(v => v.trim());
    if (!preenchido) return [];
    if (campos.some(v => !v)) throw new Error("Para cadastrar endereço, preencha rua, bairro, cidade e estado.");
    return [{ rua, numero: $("numero").value.trim(), complemento: $("complemento").value.trim(), bairro, cidade, estado, cep: $("cep").value.trim() }];
}

$("formUsuario").addEventListener("submit", async evento => {
    evento.preventDefault();
    try {
        const id = $("usuarioId").value;
        const usuario = {
            nome: $("nome").value.trim(), email: $("email").value.trim(),
            telefone: $("telefone").value.trim(), cidade: $("cidade").value.trim(),
            estado: $("estado").value.trim().toUpperCase(), enderecos: enderecoDoFormulario()
        };
        await requisicao(id ? `/usuarios/${id}` : "/usuarios", {
            method: id ? "PUT" : "POST", body: JSON.stringify(usuario)
        });
        resetarFormularioUsuario();
        mostrarMensagem(id ? "Usuário atualizado." : "Usuário cadastrado.");
        await carregarTudo();
    } catch (erro) { mostrarMensagem(erro.message, "erro"); }
});

function resetarFormularioUsuario() {
    $("formUsuario").reset(); $("usuarioId").value = "";
    $("botaoUsuario").textContent = "Cadastrar usuário";
    $("cancelarEdicaoUsuario").hidden = true;
}

$("listaUsuarios").addEventListener("click", async evento => {
    const editar = evento.target.closest("[data-editar-usuario]");
    const excluir = evento.target.closest("[data-excluir-usuario]");
    try {
        if (editar) {
            const u = estado.usuarios.find(item => item.id === editar.dataset.editarUsuario);
            if (!u) return;
            $("usuarioId").value = u.id; $("nome").value = u.nome || "";
            $("email").value = u.email || ""; $("telefone").value = u.telefone || "";
            $("cidade").value = u.cidade || ""; $("estado").value = u.estado || "";
            const e = u.enderecos?.[0];
            if (e) {
                $("rua").value = e.rua || ""; $("numero").value = e.numero || "";
                $("complemento").value = e.complemento || ""; $("bairro").value = e.bairro || "";
                $("cidadeEndereco").value = e.cidade || ""; $("estadoEndereco").value = e.estado || "";
                $("cep").value = e.cep || ""; document.querySelector(".endereco").open = true;
            }
            $("botaoUsuario").textContent = "Salvar usuário";
            $("cancelarEdicaoUsuario").hidden = false;
            location.hash = "usuarios";
        }
        if (excluir && confirm("Excluir este usuário? Não exclua usuários associados a livros ou doações.")) {
            await requisicao(`/usuarios/${excluir.dataset.excluirUsuario}`, { method: "DELETE" });
            mostrarMensagem("Usuário excluído."); await carregarTudo();
        }
    } catch (erro) { mostrarMensagem(erro.message, "erro"); }
});
$("cancelarEdicaoUsuario").addEventListener("click", resetarFormularioUsuario);

$("formLivro").addEventListener("submit", async evento => {
    evento.preventDefault();
    try {
        const id = $("livroId").value;
        const livro = {
            titulo: $("titulo").value.trim(), autor: $("autor").value.trim(),
            categoria: $("categoria").value.trim(), estadoConservacao: $("estadoConservacao").value.trim(),
            doadorId: $("doadorLivro").value || null, disponivel: $("disponivel").checked
        };
        await requisicao(id ? `/livros/${id}` : "/livros", {
            method: id ? "PUT" : "POST", body: JSON.stringify(livro)
        });
        resetarFormularioLivro(); mostrarMensagem(id ? "Livro atualizado." : "Livro cadastrado.");
        await carregarTudo();
    } catch (erro) { mostrarMensagem(erro.message, "erro"); }
});

function resetarFormularioLivro() {
    $("formLivro").reset(); $("livroId").value = ""; $("disponivel").checked = true;
    $("botaoLivro").textContent = "Cadastrar livro"; $("cancelarEdicaoLivro").hidden = true;
}

$("listaLivros").addEventListener("click", async evento => {
    const editar = evento.target.closest("[data-editar-livro]");
    const excluir = evento.target.closest("[data-excluir-livro]");
    try {
        if (editar) {
            const l = estado.livros.find(item => item.id === editar.dataset.editarLivro);
            if (!l) return;
            $("livroId").value = l.id; $("titulo").value = l.titulo || "";
            $("autor").value = l.autor || ""; $("categoria").value = l.categoria || "";
            $("estadoConservacao").value = l.estadoConservacao || "";
            $("disponivel").checked = Boolean(l.disponivel);
            $("doadorLivro").value = l.doadorId || "";
            $("botaoLivro").textContent = "Salvar alterações";
            $("cancelarEdicaoLivro").hidden = false; location.hash = "livros";
        }
        if (excluir && confirm("Excluir este livro? Verifique antes se ele possui doação vinculada.")) {
            await requisicao(`/livros/${excluir.dataset.excluirLivro}`, { method: "DELETE" });
            mostrarMensagem("Livro excluído."); await carregarTudo();
        }
    } catch (erro) { mostrarMensagem(erro.message, "erro"); }
});
$("cancelarEdicaoLivro").addEventListener("click", resetarFormularioLivro);

$("formDoacao").addEventListener("submit", async evento => {
    evento.preventDefault();
    try {
        const doacao = { livroId: $("livroDoacao").value, doadorId: $("doadorDoacao").value };
        await requisicao("/doacoes", { method: "POST", body: JSON.stringify(doacao) });
        $("formDoacao").reset(); $("doadorDoacao").disabled = false;
        mostrarMensagem("Doação registrada e histórico inicial criado.");
        await carregarTudo();
    } catch (erro) { mostrarMensagem(erro.message, "erro"); }
});

$("listaDoacoes").addEventListener("click", async evento => {
    const botao = evento.target.closest("[data-status-doacao]");
    if (!botao) return;
    try {
        const id = botao.dataset.statusDoacao;
        const status = $(`status-${id}`).value;
        const observacao = prompt("Observação para registrar no histórico (opcional):") || "";
        await requisicao(`/doacoes/${id}/status`, {
            method: "PATCH", body: JSON.stringify({ status, observacao })
        });
        mostrarMensagem("Status e histórico atualizados."); await carregarTudo();
    } catch (erro) { mostrarMensagem(erro.message, "erro"); }
});

carregarTudo();
