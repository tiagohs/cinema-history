# Apoio (compra única) — configuração no Play Console e testes

O Cinema History continua gratuito ("Cultura é gratuita. Viva o cinema."). O **apoio** é opcional,
por **pagamento único, para sempre**, só no **Brasil** e com o app em **português**. Quem apoia:

- fica **sem anúncios** (nenhum anúncio é carregado e o SDK de anúncios nem é inicializado);
- libera a **narração em áudio** dos capítulos (português; inglês e espanhol em breve).

Fora do Brasil ou com o app em inglês/espanhol, nada disso aparece (nem oferta, nem botão de áudio).

## Como o app decide

| Regra | Onde |
|---|---|
| Oferta disponível = país da conta do Google Play (`BillingClient.getBillingConfigAsync` → `countryCode`) == `BR` **e** `ContentLanguage.current() == "pt"` | `support/Supporter.kt` (`isOfferAvailable`) |
| Apoiador = existe compra **PURCHASED** de qualquer um dos 3 produtos (reconfirmada no Play ao abrir e ao voltar ao app; cache em SharedPreferences) | `support/SupportBilling.kt`, `support/SupportStore.kt` |
| Pix/boleto (**PENDING**) não libera nada até virar **PURCHASED** | `SupportBilling.handlePurchases` |
| Toda compra é **reconhecida** (acknowledge) — sem isso o Play reembolsa em 3 dias | `SupportBilling.acknowledge` |
| Reembolso/estorno: na próxima consulta sem compra válida, o apoio é removido | `SupportBilling.handlePurchases` |
| Preços exibidos vêm **sempre** do Play (`ProductDetails`), nunca do código | `support/SupportProduct.kt` |

O país fica em cache: se a primeira abertura for offline, a oferta aparece assim que o Play responder.

## 1. Pré-requisitos no Play Console

1. **Perfil de pagamentos** (conta de comerciante) ativo: *Configuração → Perfil de pagamentos*.
2. Enviar para alguma faixa de teste (teste interno basta) um AAB **com esta versão** (que já tem a
   Play Billing Library 8; a permissão `com.android.vending.BILLING` entra sozinha pelo manifesto da biblioteca).
   Sem um AAB com Billing publicado em alguma faixa, o Console não deixa criar produtos.

## 2. Criar os 3 produtos de compra única

*Monetizar com o Play → Produtos → Produtos de compra única → Criar produto de compra única.*

Crie **três** produtos. Todos têm **o mesmo benefício**; só o valor muda ("pague o quanto quiser").
Os IDs precisam ser exatamente estes (não dá para mudar depois):

| ID do produto | Nome (aparece na tela de pagamento do Google) | Preço (BRL) |
|---|---|---|
| `apoio_vitalicio` | Apoio ao Cinema History | **R$ 19,90** (lançamento R$ 14,90 — ver passo 3) |
| `apoio_vitalicio_plus` | Apoio+ ao Cinema History | **R$ 29,90** |
| `apoio_vitalicio_fa` | Apoio Fã de Cinema | **R$ 49,90** |

**Descrição sugerida** (a mesma nos três, até 200 caracteres):

> Pagamento único, para sempre: app sem anúncios e narração em áudio de todos os capítulos. Você ajuda quem pesquisa, escreve e organiza o conteúdo. Obrigado!

Para cada produto:

1. **Opção de compra**: tipo **Comprar** (compra única, não é aluguel). ID sugerido: `compra`.
   Se aparecer a opção "compatível com versões anteriores", pode deixar marcada (não atrapalha).
2. **Disponibilidade e preço**: marque **somente o Brasil** como país/região disponível e defina o
   preço em BRL da tabela acima (desmarque os demais países, ou deixe "Não disponível" neles).
3. **Não consumível**: não há nada a configurar — o app nunca "consome" a compra (ela vale para sempre).
4. **Ative** o produto (estado *Ativo*).

Na tela, o nome de cada cartão ("Apoio", "Apoio+", "Fã de cinema") vem do app; **o preço vem do Play**.
Se um produto estiver inativo ou indisponível para a conta, o cartão dele simplesmente não aparece.

## 3. Preço promocional de lançamento (R$ 14,90 no "Apoio")

No produto `apoio_vitalicio` → opção de compra `compra` → **Ofertas → Adicionar oferta → Desconto**:

- ID da oferta: `lancamento`;
- País: Brasil; **preço com desconto: R$ 14,90** (ou ~25% de desconto sobre R$ 19,90);
- Período: data de início = lançamento; data de fim = quando quiser encerrar a promoção;
- Ative a oferta.

O app escolhe automaticamente a oferta mais barata disponível para o usuário e, quando existe preço
cheio, mostra **R$ 19,90 riscado** + o selo **"Lançamento"** no cartão "Apoio". Quando a oferta
terminar, o cartão volta a mostrar R$ 19,90 sem precisar publicar nova versão.

## 4. Testar sem ser cobrado (testadores de licença)

1. *Configuração (da conta de desenvolvedor) → Teste de licença*: adicione os e-mails Gmail de quem vai
   testar e salve. Resposta de licença: `RESPOND_NORMALLY`.
2. Adicione os mesmos e-mails na lista de testadores da faixa de **teste interno** e abra o link de
   participação no aparelho.
3. A conta Google do aparelho precisa ter **país do Play = Brasil** (é o que o `getBillingConfigAsync`
   devolve) e o app precisa estar em **português**.
4. Instale o app **pela Play Store** (link do teste interno). Na tela de pagamento aparecem os
   "cartões de teste":
   - **Cartão de teste, sempre aprova** → compra concluída na hora (apoio liberado, anúncios somem);
   - **Cartão de teste, aprova depois de alguns minutos** → simula Pix/boleto (estado **pendente**:
     a tela mostra "Pagamento em processamento" e libera sozinha quando aprovar);
   - **Cartão de teste, recusa depois de alguns minutos** → pendente que nunca libera.
5. Para testar de novo: *Gestão de pedidos* → reembolse a compra de teste (marque "remover direito").
   Ao reabrir o app (ou "Restaurar compra"), o apoio é removido e os anúncios voltam.

Compras de testadores de licença não são cobradas e são canceladas automaticamente depois de um tempo.

## 5. Testar no aparelho sem conta de teste (só builds de debug)

Em **Sobre**, toque **7 vezes** na linha da versão. Abre o painel "Apoio (debug)":

- **Forçar oferta (como BR + pt)** — mostra todos os pontos de entrada e a página 4 do onboarding;
  sem acesso aos produtos do Play (app instalado fora da loja), a tela de apoio mostra valores de
  **prévia** marcados "(debug)" e a "compra" é simulada;
- **Forçar apoiador** — esconde todos os anúncios e libera o áudio;
- **Simular pagamento pendente** — mostra o cartão "Pagamento em processamento";
- **Mostrar o onboarding de novo** — na próxima abertura do app;
- **Zerar contador do cartão de fim de capítulo**.

O painel também mostra o país em cache, o idioma do conteúdo e se é apoiador.
Builds de release ignoram todas essas opções.

## 6. Pontos de entrada no app (todos passivos)

- **Onboarding** (primeira abertura, só instalação nova): página 4 "Quer ajudar?" quando a oferta existe;
- **Home → ⋮ → "Apoie o app"** (ou "Você é apoiador ♥");
- **Configurações → "Apoie o app"**;
- **Sobre → agradecimento aos apoiadores**;
- **Fim do capítulo**: um cartão discreto a cada **5 capítulos concluídos** (rolar até o fim conta como
  concluído), uma vez por marco, no fim do conteúdo — longe dos botões de navegação. Não é anúncio.

Nenhum desses pontos interrompe a leitura (sem pop-ups).

## 7. Checklist de lançamento

- [ ] 3 produtos ativos, só Brasil, preços R$ 19,90 / R$ 29,90 / R$ 49,90;
- [ ] Oferta de desconto `lancamento` (R$ 14,90) ativa no `apoio_vitalicio` com data de fim;
- [ ] Compra testada com "sempre aprova" e com "aprova depois" (pendente) por um testador de licença;
- [ ] Reembolso testado (apoio removido após reabrir o app);
- [ ] Conferido que com o app em inglês/espanhol, ou com conta de fora do Brasil, nada do apoio aparece;
- [ ] *Política do app → Conteúdo do app*: nada muda nos anúncios (os apoiadores apenas não os veem).
