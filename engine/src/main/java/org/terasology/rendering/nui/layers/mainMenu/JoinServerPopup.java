/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.layers.mainMenu;

import java.util.concurrent.Callable;

import org.terasology.config.Config;
import org.terasology.engine.GameEngine;
import org.terasology.engine.TerasologyConstants;
import org.terasology.engine.modes.StateLoading;
import org.terasology.registry.In;
import org.terasology.network.JoinStatus;
import org.terasology.network.NetworkSystem;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.ParallelOperation;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.WidgetUtil;
import org.terasology.rendering.nui.ParallelOperation.Status;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.UIText;

/**
 * @author Immortius
 */
public class JoinServerPopup extends CoreScreenLayer {

    @In
    private Config config;

    @In
    private NetworkSystem networkSystem;

    @In
    private GameEngine engine;
    
    private ParallelOperation<JoinStatus> joinServer = new ParallelOperation<>();

    @Override
    public void initialise() {
        WidgetUtil.trySubscribe(this, "join", new ActivateEventListener() {
            @Override
            public void onActivated(UIWidget button) {
                UIText address = find("address", UIText.class);
                join(address.getText());
            }
        });

        WidgetUtil.trySubscribe(this, "cancel", new ActivateEventListener() {
            @Override
            public void onActivated(UIWidget button) {
                getManager().popScreen();
            }
        });
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);

        Status status = joinServer.getStatus();

        if (status.isFinished()) {
            getManager().popScreen();

            if (status == ParallelOperation.Status.SUCCESSFUL) {
                JoinStatus result = joinServer.getResult();
                if (result.getStatus() != JoinStatus.Status.FAILED) {
                    engine.changeState(new StateLoading(result));
                } else {
                    MessagePopup screen = getManager().pushScreen(MessagePopup.ASSET_URI, MessagePopup.class);
                    screen.setMessage("Failed to Join", "Could not connect to server - " + result.getErrorMessage());
                }
            }

            joinServer = new ParallelOperation<>();
        }
    }
    
    private void join(final String address) {
        Callable<JoinStatus> action = new Callable<JoinStatus>() {

            @Override
            public JoinStatus call() throws InterruptedException {
                JoinStatus joinStatus = networkSystem.join(address, TerasologyConstants.DEFAULT_PORT);
                return joinStatus;
            }
        };

        String message = "Connecting to '" + address + "' - please wait ...";

        joinServer = new ParallelOperation<JoinStatus>(action);
        joinServer.showPopup(getManager(), "Join Game", message);
    }

    @Override
    public void onOpened() {
        super.onOpened();
        
        UIText address = find("address", UIText.class);
        getManager().setFocus(address);
    }
}
