package com.gianlu.pyxreloaded.handlers;

import com.gianlu.pyxreloaded.Consts;
import com.gianlu.pyxreloaded.JsonWrapper;
import com.gianlu.pyxreloaded.data.Game;
import com.gianlu.pyxreloaded.data.GameManager;
import com.gianlu.pyxreloaded.data.User;
import com.gianlu.pyxreloaded.servlets.Annotations;
import com.gianlu.pyxreloaded.servlets.BaseCahHandler;
import com.gianlu.pyxreloaded.servlets.Parameters;
import io.undertow.server.HttpServerExchange;
import org.apache.commons.lang3.StringEscapeUtils;

public class PlayCardHandler extends GameWithPlayerHandler {
    public static final String OP = Consts.Operation.PLAY_CARD.toString();

    public PlayCardHandler(@Annotations.GameManager GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public JsonWrapper handleWithUserInGame(User user, Game game, Parameters params, HttpServerExchange exchange) throws BaseCahHandler.CahException {
        String cardIdStr = params.get(Consts.GeneralKeys.CARD_ID);
        if (cardIdStr == null || cardIdStr.isEmpty())
            throw new BaseCahHandler.CahException(Consts.ErrorCode.NO_CARD_SPECIFIED);

        int cardId;
        try {
            cardId = Integer.parseInt(cardIdStr);
        } catch (NumberFormatException ex) {
            throw new BaseCahHandler.CahException(Consts.ErrorCode.INVALID_CARD, ex);
        }

        String text = params.get(Consts.GeneralKeys.WRITE_IN_TEXT);
        if (text != null && text.contains("<")) text = StringEscapeUtils.escapeXml11(text);

        return new JsonWrapper(Consts.OngoingGameData.LEFT_TO_PLAY, game.playCard(user, cardId, text));
    }
}