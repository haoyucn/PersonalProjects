package communication;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class IResultAdapter implements JsonSerializer<Result>, JsonDeserializer<Result> {

    private static final String SUCCESS = "SUCCESS";
    private static final String DATA = "DATA";
    private static final String DATACLASS = "DATACLASS";
    private static final String ERRORINFO = "ERRORINFO";

    @Override
    public Result deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        JsonObject json = jsonElement.getAsJsonObject();

        boolean success = json.get(SUCCESS).getAsBoolean();
        Object data = null;
        String errorInfo = null;

        if(success) {
            JsonPrimitive prim = (JsonPrimitive) json.get(DATACLASS);
            String className = prim.getAsString();

            Class<?> klass = null;
            try{
                klass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }

            data = context.deserialize(json.get(DATA), klass);
        }
        else errorInfo = json.get(ERRORINFO).getAsString();

        return new Result(success, data, errorInfo);
    }

    @Override
    public JsonElement serialize(Result result, Type type, JsonSerializationContext context) {

        JsonObject jsonResult = new JsonObject();

        jsonResult.addProperty(SUCCESS, result.isSuccess());

        if(!result.isSuccess()) {
            jsonResult.addProperty(DATACLASS, "null");
            jsonResult.addProperty(DATA, "null");
            jsonResult.addProperty(ERRORINFO, result.getErrorInfo());
        }
        else{
            String dataName = result.getData().getClass().getName();
            jsonResult.addProperty(DATACLASS, dataName);
            JsonElement data = context.serialize(result.getData());
            jsonResult.add(DATA, data);
            jsonResult.addProperty(ERRORINFO, "null");
        }

        return jsonResult;
    }
}
