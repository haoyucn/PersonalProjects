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

public class ICommandAdapter implements JsonSerializer<ICommand>, JsonDeserializer<ICommand> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE = "INSTANCE";

    @Override
    public ICommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        JsonObject json = jsonElement.getAsJsonObject();
        JsonPrimitive prim = json.getAsJsonPrimitive(CLASSNAME);
        String className = prim.getAsString();

        Class<?> klass = null;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new JsonParseException(e.getMessage());
        }

        return context.deserialize(json.get(INSTANCE), klass);
    }

    @Override
    public JsonElement serialize(ICommand iCommand, Type type, JsonSerializationContext context) {

        JsonObject commandJson = new JsonObject();
        String className = iCommand.getClass().getName();
        commandJson.addProperty(CLASSNAME, className);
        JsonElement elem = context.serialize(iCommand);
        commandJson.add(INSTANCE, elem);

        return commandJson;
    }
}
