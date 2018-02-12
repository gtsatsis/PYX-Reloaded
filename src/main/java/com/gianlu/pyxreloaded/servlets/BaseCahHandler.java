package com.gianlu.pyxreloaded.servlets;

import com.gianlu.pyxreloaded.Consts;
import com.gianlu.pyxreloaded.JsonWrapper;
import com.gianlu.pyxreloaded.data.User;
import com.google.gson.JsonElement;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.StatusCodes;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public abstract class BaseCahHandler extends BaseJsonHandler {

    @Override
    protected JsonElement handle(HttpServerExchange exchange) throws StatusException {
        Cookie sid = exchange.getRequestCookies().get("PYX-Session");
        User user = null;
        if (sid != null) user = Sessions.getUser(sid.getValue());

        Parameters params;
        try {
            params = Parameters.fromExchange(exchange, (int) exchange.getRequestContentLength());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new StatusException(StatusCodes.INTERNAL_SERVER_ERROR, ex);
        }

        String op = params.get(Consts.GeneralKeys.OP);
        boolean skipUserCheck = Objects.equals(op, Consts.Operation.REGISTER.toString()) || Objects.equals(op, Consts.Operation.FIRST_LOAD.toString());
        if (!skipUserCheck && user == null) {
            throw new CahException(Consts.ErrorCode.NOT_REGISTERED);
        } else if (user != null && !user.isValid()) {
            Sessions.invalidate(sid.getValue());
            throw new CahException(Consts.ErrorCode.SESSION_EXPIRED);
        } else {
            return handleRequest(op, user, params, exchange);
        }
    }

    protected abstract JsonElement handleRequest(@Nullable String op, @Nullable User user, Parameters params, HttpServerExchange exchange) throws StatusException;

    public static class CahException extends StatusException {
        public final Consts.ErrorCode code;
        public final JsonWrapper data;

        public CahException(Consts.ErrorCode code) {
            super(StatusCodes.CONFLICT);
            this.code = code;
            this.data = null;
        }

        public CahException(Consts.ErrorCode code, Throwable cause) {
            super(StatusCodes.CONFLICT, cause);
            this.code = code;
            this.data = null;
        }

        public CahException(Consts.ErrorCode code, JsonWrapper data) {
            super(StatusCodes.CONFLICT);
            this.code = code;
            this.data = data;
        }
    }
}