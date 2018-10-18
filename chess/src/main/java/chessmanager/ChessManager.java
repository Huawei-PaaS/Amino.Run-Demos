package chessmanager;

/**
 * Created by mbssaiakhil on 27/1/18.
 */

import engine.SimpleEngine;
import sapphire.app.SapphireObject;

import static sapphire.runtime.Sapphire.new_;

public class ChessManager implements SapphireObject {

    private SimpleEngine simpleEngine;

    public ChessManager() {
        simpleEngine = (SimpleEngine) new_(SimpleEngine.class);
    }

    public SimpleEngine getSimpleEngine() { return simpleEngine; }
}
