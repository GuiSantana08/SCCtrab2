package scc.srv;

import java.util.ArrayList;
import java.util.List;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import scc.azure.cache.RedisCache;
import scc.azure.db.QuestionDBLayer;
import scc.data.Question;
import scc.data.QuestionDAO;
import scc.interfaces.QuestionResourceInterface;

@Path("/house/{id}/question")
public class QuestionResource implements QuestionResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    QuestionDBLayer questionDb = QuestionDBLayer.getInstance();

    static RedisCache cache = RedisCache.getInstance();

    @Override
    public Response createQuestion(boolean isCacheActive, boolean isAuthActive, Cookie session, String id,
            Question question) {
        try {
            if (isAuthActive) {
                UserResource.checkCookieUser(session, question.getPostUserId());
            }

            QuestionDAO qDAo = new QuestionDAO(question);
            qDAo.setHouse(id);
            CosmosItemResponse<QuestionDAO> q = questionDb.putQuestion(qDAo);

            if (isCacheActive) {
                cache.setValue(question.getId(), question);
            }

            return Response.ok(q.getItem().toString()).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteQuestion(boolean isCacheActive, boolean isAuthActive, Cookie session, String id) {
        try {
            if (isAuthActive) {
                // UserResource.checkCookieUser(session, house.getUserId()); TODO
            }

            questionDb.delQuestionById(id);

            if (isCacheActive) {
                cache.delete(id, QuestionDAO.class);
            }

            return Response.ok().build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listQuestions(String id) {
        List<QuestionDAO> questions = new ArrayList<>();
        try {

            CosmosPagedIterable<QuestionDAO> u = questionDb.getHouseQuestions(id);

            while (u.iterator().hasNext()) {
                questions.add(u.iterator().next());
            }

            return Response.ok(questions).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getLocalizedMessage()).build();
        }
    }
}
