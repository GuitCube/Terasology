/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.layers.ingame.metrics;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.StandComponent;
import org.terasology.logic.characters.WalkComponent;
import org.terasology.logic.common.InspectionToolComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.rendering.assets.material.Material;
import org.terasology.rendering.assets.skeletalmesh.SkeletalMesh;
import org.terasology.rendering.logic.SkeletalMeshComponent;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.widgets.*;

import java.util.ArrayList;

/**
 */
public class AnimationScreen extends CoreScreenLayer {
    private UIText fullDescriptionLabel;
    private UIText entityIdField;
    private UIButton spawnEntityIdButton;
    private UISlider animationSpeedSlider;
    @In
    private EntityRef entityRef;
    @In
    private LocalPlayer localPlayer;
    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;

    private static final Logger logger = LoggerFactory.getLogger(AnimationScreen.class);


    @Override
    public void initialise() {
        spawnEntityIdButton = find("spawnEntityIdButton", UIButton.class);
        UIDropdown<ResourceUrn> entityDropdown = find("entityDropdown", UIDropdownScrollable.class);
        logger.info("Number of available skeletal meshes: " + assetManager.getAvailableAssets(SkeletalMesh.class).size());
        if (entityDropdown != null) {
            entityDropdown.setOptions(new ArrayList(assetManager.getAvailableAssets(SkeletalMesh.class)));
        }
        animationSpeedSlider = find("entityAnimationSpeedSlider", UISlider.class);
        if (animationSpeedSlider != null) {
            animationSpeedSlider.setMinimum(-2.5f);
            animationSpeedSlider.setIncrement(0.1f);
            animationSpeedSlider.setRange(5.0f);
            animationSpeedSlider.setPrecision(1);
        }
        spawnEntityIdButton.subscribe(widget -> {
            Vector3f localPlayerPosition = localPlayer.getPosition();
            Quat4f localPlayerRotation = localPlayer.getRotation();
            java.util.Optional<Prefab> prefab = assetManager.getAsset(entityDropdown.getSelection(), Prefab.class);
            if (prefab.isPresent() && prefab.get().getComponent(LocationComponent.class) != null)
            entityRef = entityManager.create(prefab.get(), localPlayerPosition, localPlayerRotation);
            SkeletalMeshComponent skeletalMeshComponent = entityRef.getComponent(SkeletalMeshComponent.class);
            skeletalMeshComponent.animationRate = animationSpeedSlider.getValue();
            entityRef.saveComponent(skeletalMeshComponent);
//            StandComponent standComponent = entityRef.getComponent(StandComponent.class);
//            standComponent.animationPool.clear();
//            WalkComponent walk = entityRef.getComponent(WalkComponent.class);
//            walk.animationPool.clear();
            CharacterMovementComponent movementComponent = entityRef.getComponent(CharacterMovementComponent.class);
            movementComponent.speedMultiplier = animationSpeedSlider.getValue();
            entityRef.saveComponent(movementComponent);
//            Vector3f offset = new Vector3f(localPlayer.getRotation());
//            ClientComponent clientComponent = sender.getComponent(ClientComponent.class);
//            LocationComponent characterLocation = clientComponent.character.getComponent(LocationComponent.class);
//            Vector3f spawnPos = characterLocation.getWorldPosition();
//            Vector3f offset = new Vector3f(characterLocation.getWorldDirection());
//            offset.scale(2);
//            spawnPos.add(offset);
//            Vector3f dir = new Vector3f(characterLocation.getWorldDirection());
//            dir.y = 0;
//            if (dir.lengthSquared() > 0.001f) {
//                dir.normalize();
//            } else {
//                dir.set(Direction.FORWARD.getVector3f());
//            }
//            Quat4f rotation = Quat4f.shortestArcQuat(Direction.FORWARD.getVector3f(), dir);
//            java.util.Optional<Prefab> prefab = Assets.getPrefab((entityDropdown.getSelection()));
//            String abcd = entityDropdown.getSelection();
//            Optional<SkeletalMesh> optionalSkel = assetManager.getAsset(entityDropdown.getSelection(), SkeletalMesh.class);
//            Optional<Material> optional = assetManager.getAsset(entityDropdown.getSelection(), Material.class);
//            SkeletalMesh mesh = assetManager.getAsset(entityDropdown.getSelection(), SkeletalMesh.class).get();
//            Material material = assetManager.getAsset(entityDropdown.getSelection(), Material.class).get();
//            logger.info("********* " + entityDropdown.getSelection());
//            EntityBuilder entityBuilder = entityManager.newBuilder();
//            SkeletalMeshComponent skeletalMesh = new SkeletalMeshComponent();
//            skeletalMesh.mesh = mesh;
//            skeletalMesh.material = material;
//            entityBuilder.addComponent(skeletalMesh);
//            entityBuilder.build();
        });

    }
}

