package authenticators;

import com.google.inject.Inject;
import models.Session;
import play.mvc.Http;
import services.SessionService;

/**
 * @authur fabiomazzone
 */
public class ActiveSessionAuthenticator extends SessionAuthenticator {
    @Inject
    public ActiveSessionAuthenticator(SessionService sessionService)
    {
        super(sessionService);
    }

    @Override
    public String getUsername(Http.Context ctx)
    {
        Session session = this.getSessionByRequest(ctx.request());

        if (session != null && sessionService.isActive(session)) {
            return session.getUsername();
        }
        return null;
    }

}
