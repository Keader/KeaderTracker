package dev.keader.correiostracker.view.adapters

import dev.keader.correiostracker.R
import timber.log.Timber

@Suppress("unused")
enum class CorreiosResourceStatus(
    val statusString: String,
    val iconRes: Int,
    val bgColorRes: Int,
    val titleColorRes: Int,
    val codeColorRes: Int
) {
    POSTED(
        statusString = "Objeto postado",
        iconRes = R.drawable.ic_h_post,
        bgColorRes = R.color.bg_posted,
        titleColorRes = R.color.title_posted,
        codeColorRes = R.color.code_posted
    ),
    MOVING(
        statusString = "Objeto em trânsito - por favor aguarde",
        iconRes = R.drawable.ic_moving,
        bgColorRes = R.color.bg_moving,
        titleColorRes = R.color.title_moving,
        codeColorRes = R.color.code_moving
    ),
    MOVING_2(
        statusString = "Objeto em trânsito",
        iconRes = R.drawable.ic_moving,
        bgColorRes = R.color.bg_moving,
        titleColorRes = R.color.title_moving,
        codeColorRes = R.color.code_moving
    ),
    ARRIVED(
        statusString = "Objeto recebido pelos Correios do Brasil",
        iconRes = R.drawable.ic_arrived,
        bgColorRes = R.color.bg_arrived,
        titleColorRes = R.color.title_arrived,
        codeColorRes = R.color.code_arrived
    ),
    ARRIVED_2(
        statusString = "Objeto recebido na unidade de exportação no país de origem",
        iconRes = R.drawable.ic_arrived,
        bgColorRes = R.color.bg_arrived,
        titleColorRes = R.color.title_arrived,
        codeColorRes = R.color.code_arrived
    ),
    INSPECTION(
        statusString = "Encaminhado para fiscalização aduaneira",
        iconRes = R.drawable.ic_inspection,
        bgColorRes = R.color.bg_inspection,
        titleColorRes = R.color.title_inspection,
        codeColorRes = R.color.code_inspection
    ),
    INSPECTION_ANVISA(
        statusString = "Objeto encaminhado para fiscalização - ANVISA",
        iconRes = R.drawable.ic_inspection,
        bgColorRes = R.color.bg_inspection,
        titleColorRes = R.color.title_inspection,
        codeColorRes = R.color.code_inspection
    ),
    INSPECTION_EXP(
        statusString = "Objeto encaminhado para fiscalização aduaneira de exportação",
        iconRes = R.drawable.ic_inspection,
        bgColorRes = R.color.bg_inspection,
        titleColorRes = R.color.title_inspection,
        codeColorRes = R.color.code_inspection
    ),
    REVIEW_TAXES(
        statusString = "Solicitação de revisão do tributo",
        iconRes = R.drawable.ic_inspection,
        bgColorRes = R.color.bg_inspection,
        titleColorRes = R.color.title_inspection,
        codeColorRes = R.color.code_inspection
    ),
    WAITING_PAYMENT(
        statusString = "Aguardando pagamento",
        iconRes = R.drawable.ic_block,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    WAITING_PAYMENT_2(
        statusString = "Aguardando pagamento do despacho postal",
        iconRes = R.drawable.ic_block,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    PAYMENT_OVER(
        statusString = "Prazo de pagamento encerrado",
        iconRes = R.drawable.ic_block,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    PAYMENT_REVISION(
        statusString = "Revisão de tributos solicitada pelo cliente",
        iconRes = R.drawable.ic_block,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    PAYMENT(
        statusString = "Pagamento confirmado",
        iconRes = R.drawable.ic_pay,
        bgColorRes = R.color.bg_payment,
        titleColorRes = R.color.title_payment,
        codeColorRes = R.color.code_payment
    ),
    NOT_NEED_PAYMENT(
        statusString = "Liberado sem tributação",
        iconRes = R.drawable.ic_inspection_ok,
        bgColorRes = R.color.bg_payment,
        titleColorRes = R.color.title_payment,
        codeColorRes = R.color.code_payment
    ),
    INSPECTION_FINISHED(
        statusString = "Fiscalização aduaneira finalizada",
        iconRes = R.drawable.ic_inspection_ok,
        bgColorRes = R.color.bg_inspection_ok,
        titleColorRes = R.color.title_inspection_ok,
        codeColorRes = R.color.code_inspection_ok
    ),
    DELIVERY_IN_PROGRESS(
        statusString = "Objeto saiu para entrega ao destinatário",
        iconRes = R.drawable.ic_delivery_in_progress,
        bgColorRes = R.color.bg_delivery_in_progress,
        titleColorRes = R.color.title_delivery_in_progress,
        codeColorRes = R.color.code_delivery_in_progress
    ),
    DELIVERY_IN_PROGRESS_2(
        statusString = "Objeto saiu para entrega ao remetente",
        iconRes = R.drawable.ic_delivery_in_progress,
        bgColorRes = R.color.bg_delivery_in_progress,
        titleColorRes = R.color.title_delivery_in_progress,
        codeColorRes = R.color.code_delivery_in_progress
    ),
    WAITING_POST(
        statusString = "Aguardando postagem pelo remetente",
        iconRes = R.drawable.ic_waiting_post,
        bgColorRes = R.color.bg_waiting_post,
        titleColorRes = R.color.title_waiting_post,
        codeColorRes = R.color.code_waiting_post
    ),
    WAITING_CORREIOS(
        statusString = "Carteiro saiu para coleta do objeto",
        iconRes = R.drawable.ic_waiting_post,
        bgColorRes = R.color.bg_waiting_post,
        titleColorRes = R.color.title_waiting_post,
        codeColorRes = R.color.code_waiting_post
    ),
    WAITING_POST_OLD(
        statusString = "Aguardando postagem pelo remetente.",
        iconRes = R.drawable.ic_waiting_post,
        bgColorRes = R.color.bg_waiting_post,
        titleColorRes = R.color.title_waiting_post,
        codeColorRes = R.color.code_waiting_post
    ),
    DELIVERED(
        statusString = "Objeto entregue ao destinatário",
        iconRes = R.drawable.ic_delivered,
        bgColorRes = R.color.bg_delivered,
        titleColorRes = R.color.title_delivered,
        codeColorRes = R.color.code_delivered
    ),
    BROKEN(
        statusString = "Objeto recebido pelos Correios do Brasil com embalagem danificada",
        iconRes = R.drawable.ic_broken,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    CRASH(
        statusString = "Objeto e/ou conteúdo avariado por acidente com veículo",
        iconRes = R.drawable.ic_broken,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    POSTED_DELAYED(
        statusString = "Objeto postado após o horário limite da unidade",
        iconRes = R.drawable.ic_h_post,
        bgColorRes = R.color.bg_posted,
        titleColorRes = R.color.title_posted,
        codeColorRes = R.color.code_posted
    ),
    DELIVERY_IN_PROGRESS_ADDRESS(
        statusString = "Objeto encaminhado para retirada no endereço indicado",
        iconRes = R.drawable.ic_delivery_in_progress,
        bgColorRes = R.color.bg_delivery_in_progress,
        titleColorRes = R.color.title_delivery_in_progress,
        codeColorRes = R.color.code_delivery_in_progress
    ),
    NOT_PAID(
        statusString = "Pagamento não confirmado pela operadora",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_DELIVERED(
        statusString = "Objeto não entregue - carteiro não atendido",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_DELIVERED_2(
        statusString = "Objeto não entregue - endereço incorreto",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_DELIVERED_3(
        statusString = "Carteiro não atendido - Entrega não realizada",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_DELIVERED_4(
        statusString = "Objeto não entregue - cliente desconhecido no local",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_DELIVERED_5(
        statusString = "A entrega não pode ser efetuada",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_MOVING(
        statusString = "Objeto mal encaminhado",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    STOLEN(
        statusString = "Objeto roubado dos Correios",
        iconRes = R.drawable.ic_police,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_AUTHORIZED(
        statusString = "A entrada do objeto no Brasil não foi autorizada pelos órgãos fiscalizadores",
        iconRes = R.drawable.ic_police,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    DELIVERED_2(
        statusString = "Objeto entregue ao remetente",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_delivered,
        titleColorRes = R.color.title_delivered,
        codeColorRes = R.color.code_delivered
    ),
    WAREHOUSE(
        statusString = "Objeto recebido na unidade de distribuição",
        iconRes = R.drawable.ic_warehouse,
        bgColorRes = R.color.bg_posted,
        titleColorRes = R.color.title_posted,
        codeColorRes = R.color.code_posted
    ),
    FIX_ROUTE(
        statusString = "Objeto em correção de rota",
        iconRes = R.drawable.ic_moving,
        bgColorRes = R.color.bg_moving,
        titleColorRes = R.color.title_moving,
        codeColorRes = R.color.code_moving
    ),
    ADDRESS_NOT_FOUND(
        statusString = "Objeto não entregue - Endereço não encontrado",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    DELIVERY_CANCELED(
        statusString = "Saída para entrega cancelada",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    WAITING_USER(
        statusString = "Objeto aguardando retirada no endereço indicado",
        iconRes = R.drawable.ic_waiting_user,
        bgColorRes = R.color.bg_posted,
        titleColorRes = R.color.title_posted,
        codeColorRes = R.color.code_posted
    ),
    ACTION_NEEDED(
        statusString = "Faltam informações. Sua ação é necessária",
        iconRes = R.drawable.ic_waiting_user,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    EXPIRED(
        statusString = "Prazo de retirada pelo destinatário encerrado",
        iconRes = R.drawable.ic_waiting_user,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    WAITING_BOX(
        statusString = "Objeto disponível para retirada em Caixa Postal",
        iconRes = R.drawable.ic_waiting_user,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    NOT_UNIT(
        statusString = "Objeto ainda não chegou à unidade",
        iconRes = R.drawable.ic_moving,
        bgColorRes = R.color.bg_moving,
        titleColorRes = R.color.title_moving,
        codeColorRes = R.color.code_moving
    ),
    AREA_NOT_SAFE(
        statusString = "Área com distribuição sujeita a prazo diferenciado",
        iconRes = R.drawable.ic_moving,
        bgColorRes = R.color.bg_moving,
        titleColorRes = R.color.title_moving,
        codeColorRes = R.color.code_moving
    ),
    NOT_FOUND(
        statusString = "Objeto não localizado no fluxo postal",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    SUSPENSION(
        statusString = "Solicitação de suspensão de entrega recebida",
        iconRes = R.drawable.ic_blocked,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    RETURN_CORREIOS(
        statusString = "Objeto devolvido aos Correios",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    RETURN_COUNTRY(
        statusString = "Objeto devolvido ao país de origem",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    RETURN(
        statusString = "Devolução autorizada pela Receita Federal",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    RETURN_2(
        statusString = "Objeto em devolução",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    RETURN_3(
        statusString = "Direcionado para entrega interna a pedido do cliente",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    RETURN_4(
        statusString = "Objeto será devolvido por solicitação do contratante/remetente",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    REFUSED(
        statusString = "Destinatário recusou o objeto",
        iconRes = R.drawable.ic_return,
        bgColorRes = R.color.bg_waiting_payment,
        titleColorRes = R.color.title_waiting_payment,
        codeColorRes = R.color.code_waiting_payment
    ),
    DELIVERY_FAIL(
        statusString = "Tentativa de entrega não efetuada",
        iconRes = R.drawable.ic_moving,
        bgColorRes = R.color.bg_arrived,
        titleColorRes = R.color.title_arrived,
        codeColorRes = R.color.code_arrived
    ),
    UNKNOWN(
        statusString = "",
        iconRes = R.drawable.ic_moving,
        bgColorRes = R.color.bg_unk,
        titleColorRes = R.color.title_unk,
        codeColorRes = R.color.code_unk
    );

    companion object {
        private val map = values().associateBy(CorreiosResourceStatus::statusString)
        fun getResourceStatus(status: String): CorreiosResourceStatus {
            val obj = map[status]
            if (obj != null) {
                return obj
            }

            Timber.e("Unknown Status: $status")
            return UNKNOWN
        }
    }
}
