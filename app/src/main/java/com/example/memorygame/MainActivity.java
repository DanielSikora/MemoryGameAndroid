package com.example.memorygame;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ImageView[] cards = new ImageView[8];
    private int[] cardImages =
            {R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4};//Deklarujemy pary obrazków (2 razy bo tablica ma 8 elementów)

    private int moveCount = 0;
    private TextView moveCountText;
    private Button resetButton;

    private int firstCardIndex = -1;//Dajemy -1 dlatego, że karta jest nieodkryta
    int secondCardIndex = -1;
    private boolean isFlipping = false;
    private int matchedPairs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveCountText = findViewById(R.id.moveCount);
        resetButton = findViewById(R.id.resetButton);

        // Przypisz karty (ImageView) do tablicy
        cards[0] = findViewById(R.id.image11);
        cards[1] = findViewById(R.id.image12);
        cards[2] = findViewById(R.id.image21);
        cards[3] = findViewById(R.id.image22);
        cards[4] = findViewById(R.id.image31);
        cards[5] = findViewById(R.id.image32);
        cards[6] = findViewById(R.id.image41);
        cards[7] = findViewById(R.id.image42);

        // Tasowanie kart
        shuffleCards();

        // Ustawianie OnClickListener dla każdej karty
        for (int i = 0; i < cards.length; i++) {
            final int index = i;
            cards[i].setOnClickListener(v -> {
                if (!isFlipping && cards[index].getTag() == null) {
                    flipCard(index);
                }
            });
        }

        // Ustawienie buttona odpowiadającego za reset gry
        resetButton.setOnClickListener(v -> resetGame());
    }

    // Funkcja tasująca karty
    private void shuffleCards() {
        // Konwersja tablicy na listę
        Integer[] cardImagesInteger = Arrays.stream(cardImages).boxed().toArray(Integer[]::new);
        Collections.shuffle(Arrays.asList(cardImagesInteger));  // Tasowanie listy
        //boxed konwertuje listę int na listę obiektów integer

        // Przekształcenie listy z powrotem na tablicę
        for (int i = 0; i < cardImages.length; i++) {
            cardImages[i] = cardImagesInteger[i];
        }

        // Resetowanie stanu kart
        for (ImageView card : cards) {
            card.setImageResource(R.drawable.card_back);  // Zakrywamy karty
            card.setTag(null);  // Resetujemy tagi kart
        }

        // Resetowanie liczników i wskaźników
        moveCount = 0;
        matchedPairs = 0;
        moveCountText.setText("Ruchy: " + moveCount);
        firstCardIndex = -1;
        secondCardIndex = -1;
        isFlipping = false;
    }

    // Funkcja odwracająca kartę
    private void flipCard(int index) {
        cards[index].setImageResource(cardImages[index]);
        cards[index].setTag(cardImages[index]); // Zaznaczamy, że karta jest odkryta

        if (firstCardIndex == -1) {
            firstCardIndex = index; // Pierwsza karta odkryta
        } else if (secondCardIndex == -1) {
            secondCardIndex = index; // Druga karta odkryta

            isFlipping = true; // Blokujemy kolejne ruchy w trakcie porównania kart

            new Handler(Looper.getMainLooper()).postDelayed(() -> {//tworzymy obiekt klasy handler
                // postDelayed pozwala wykonać kod po określonym opóźnieniu
                //Looper.getMainLooper()) pobiera pętle komunikatów  głównego wątku przez co handler działa na głównym wątku aplikacji
                if (cardImages[firstCardIndex] == cardImages[secondCardIndex]) {
                    // Dopasowanie! Karty pozostają odkryte
                    cards[firstCardIndex].setClickable(false);
                    cards[secondCardIndex].setClickable(false);
                    matchedPairs++;
                    if (matchedPairs == 4) {
                        // Wyświetlenie alertu o wygranej
                        showWinAlert();
                    }
                } else {
                    // Brak dopasowania, zakrywamy karty
                    cards[firstCardIndex].setImageResource(R.drawable.card_back);
                    cards[firstCardIndex].setTag(null);
                    cards[secondCardIndex].setImageResource(R.drawable.card_back);
                    cards[secondCardIndex].setTag(null);
                }

                // Zresetowanie indeksów kart
                firstCardIndex = -1;
                secondCardIndex = -1;
                isFlipping = false;

                // Aktualizacja liczby ruchów
                moveCount++;
                moveCountText.setText("Ruchy: " + moveCount);
            }, 1000); //
        }
    }

    // Funkcja do resetowania gry
    private void resetGame() {
        // Tasowanie kart
        shuffleCards();

        // Resetowanie stanu kart
        for (ImageView card : cards) {
            card.setImageResource(R.drawable.card_back); // Zakrywamy karty
            card.setTag(null); // Resetujemy tagi kart
            card.setClickable(true); // Ustawiamy karty jako klikalne
        }

        // Resetowanie liczników
        moveCount = 0;
        matchedPairs = 0;
        moveCountText.setText("Ruchy: " + moveCount);

        // Resetowanie indeksów kart
        firstCardIndex = -1;
        secondCardIndex = -1;
        isFlipping = false;
    }

    // Wyświetlenie alertu o wygranej
    private void showWinAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Gratulacje!")
                .setMessage("Wygrałeś grę w " + (moveCount+1)  + " ruchach.")
                .setPositiveButton("OK", (dialog, which) -> resetGame())
                .show();
    }
}
