//DEPS com.google.code.gson:gson:2.10.1

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CryptoPriceFetcher {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Fatal Error: Missing required argument.");
            System.err.println("Usage: CryptoPriceFetcher <crypto_name_or_symbol>");
            System.exit(1);
        }

        String query = args[0].trim();
        String symbol = resolveSymbol(query);

        try {
            String apiUrl = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Fatal Error: API request failed with HTTP status " + response.statusCode());
                System.err.println("Response Body: " + response.body());
                System.err.println("Ensure you provided a valid cryptocurrency name or symbol (e.g., 'bitcoin', 'ETH').");
                System.exit(1);
            }

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            
            if (!jsonObject.has("price")) {
                System.err.println("Fatal Error: Unexpected API response format. Missing 'price'.");
                System.exit(1);
            }

            String returnedSymbol = jsonObject.get("symbol").getAsString();
            String priceStr = jsonObject.get("price").getAsString();

            double price = Double.parseDouble(priceStr);

            System.out.printf("The current price of %s is $%.4f%n", returnedSymbol, price);

        } catch (Exception e) {
            System.err.println("Fatal Error: An exception occurred while fetching the cryptocurrency price.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String resolveSymbol(String input) {
        String upper = input.trim().toUpperCase();
        switch (upper) {
            case "BITCOIN": return "BTCUSDT";
            case "ETHEREUM": return "ETHUSDT";
            case "LITECOIN": return "LTCUSDT";
            case "RIPPLE": return "XRPUSDT";
            case "DOGECOIN": return "DOGEUSDT";
            case "CARDANO": return "ADAUSDT";
            case "SOLANA": return "SOLUSDT";
            case "POLKADOT": return "DOTUSDT";
            case "CHAINLINK": return "LINKUSDT";
            case "POLYGON": return "MATICUSDT";
            case "AVALANCHE": return "AVAXUSDT";
            case "BINANCE COIN":
            case "BNB": return "BNBUSDT";
            default:
                if (!upper.endsWith("USDT") && !upper.endsWith("BTC") && !upper.endsWith("ETH") && !upper.endsWith("BNB") && !upper.endsWith("BUSD") && !upper.endsWith("USDC") && !upper.endsWith("FDUSD")) {
                    return upper + "USDT";
                }
                return upper;
        }
    }
}