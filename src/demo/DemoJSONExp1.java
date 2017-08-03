package demo;

import kotlin.Unit;
import org.frice.game.Game;
import org.frice.game.anim.move.SimpleMove;
import org.frice.game.obj.FObject;
import org.frice.game.obj.button.SimpleText;
import org.frice.game.obj.sub.ShapeObject;
import org.frice.game.resource.graphics.ColorResource;
import org.frice.game.utils.data.Database;
import org.frice.game.utils.data.JSONPreference;
import org.frice.game.utils.graphics.shape.FCircle;
import org.frice.game.utils.graphics.shape.FRectangle;
import org.frice.game.utils.time.FTimer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;

public class DemoJSONExp1 extends Game {
    private Database db;
    private int score = 0;
    private boolean[] key = {false,false,false,false};
    private FObject player;
    private SimpleText text;
    private FTimer timer = new FTimer(1000 / 60);
    private FTimer newEntity = new FTimer(1000 / 2);
    private Random random = new Random();
    public DemoJSONExp1() throws IOException {
        super(2);
    }
    public static void main(String[] args) {
        try {
            launch(DemoJSONExp1.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override public void onInit() {
        super.onInit();
        try {
            db = new JSONPreference("database.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new ShapeObject(ColorResource.清真绿, new FCircle(20), 0, 240);
        text = new SimpleText("Highest Score: " + db.query("highestScore", 0).toString(), 10, 10);
        this.setSize(800,500);
        this.setTitle("JSON Demo");
        this.addObject(0, player);
        this.addObject(text);
        this.addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {

            }
            @Override public void keyPressed(KeyEvent e) {
                if((e.getKeyCode() <= KeyEvent.VK_DOWN) && (e.getKeyCode() >= KeyEvent.VK_LEFT)) {
                    key[e.getKeyCode() - 0x25] = true;
                }
            }
            @Override public void keyReleased(KeyEvent e) {
                if((e.getKeyCode() <= KeyEvent.VK_DOWN) && (e.getKeyCode() >= KeyEvent.VK_LEFT)) {
                    key[e.getKeyCode() - 0x25] = false;
                }
            }
        });
    }
    @Override public void onRefresh() {
        super.onRefresh();
        if(timer.ended()) {
            if(key[0] && (player.getX() > 0)) {
                player.setX(player.getX() - 1);
            }
            if(key[1] && (player.getY() > 0)) {
                player.setY(player.getY() - 1);
            }
            if(key[2] && (player.getX() < 755)) {
                player.setX(player.getX() + 1);
            }
            if(key[3] && (player.getY() < 430)) {
                player.setY(player.getY() + 1);
            }
        }
        if(newEntity.ended()) {
            score++;
            if(score >= db.queryT("highestScore", 0)) {
                db.insert("highestScore", score);
            }
            text.setText("Highest Score: " + db.query("highestScore", 0).toString());
            ShapeObject entity = new ShapeObject(ColorResource.基佬紫, new FRectangle(20, 20), 800, random.nextInt(480));
            entity.addAnim(new SimpleMove(-20 - random.nextInt(50), 10 - random.nextInt(20)));
            entity.addCollider(player, () -> {
                this.gameover();
                return Unit.INSTANCE;
            });
            this.addObject(entity);
        }
    }
    private void gameover() {
        score = 0;
        this.clearObjects();
        player = new ShapeObject(ColorResource.清真绿, new FCircle(20), 0, 240);
        text = new SimpleText("Highest Score: " + db.query("highestScore", 0).toString(), 10, 10);
        this.addObject(player);
        this.addObject(text);
        db.insert("death", db.queryT("death", 0) + 1);
    }
}