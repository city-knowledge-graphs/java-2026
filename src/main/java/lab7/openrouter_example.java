package lab7;


import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;


public class openrouter_example {

	
	 public static String readApiKey(String path) throws IOException {
	        return Files.readString(Path.of(path)).trim();
	    }
	
	
    public static void main(String[] args) throws Exception {
    	
    	
    	String prompt;
    	prompt = "Is the concept Trapezoid, with parent Bone, equivalent to Trapezoid, with parent polygon? Reply only with Yes or No.";
    	//prompt = "Is the concept Lymphokine equivalent to the concept Therapeutic Lymphokine? Reply only with Yes or No.";
    	//prompt = "If something is a Therapeutic Lymphokine, is it also Lymphokine? Reply only with Yes or No.";
    	//prompt = "If something is a Lymphokine, is it also a Therapeutic Lymphokine? Reply only with Yes or No.";
    	String key =  readApiKey("files/openrouter.key");
    	String web = "https://github.com/city-knowledge-graphs";
    	String sitename = "City Knowledge Graphs";
    	String llm;
    	llm = "qwen/qwen3-8b";
    	
    	

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(key)
                .baseUrl("https://openrouter.ai/api/v1")
                .putHeader("HTTP-Referer", web)
                .putHeader("X-OpenRouter-Title", sitename)
                .build();

        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model(llm)
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion =
                client.chat().completions().create(params);

        System.out.println("LLM: " + llm + "\nPrompt: " + prompt + "\nAnswer: " +  
                completion.choices().get(0).message().content().get().toString()
        );
    }
}
