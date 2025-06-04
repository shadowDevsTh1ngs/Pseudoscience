package io.github.shadowdevsthings.pseudoscience;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class MachineScreen extends HandledScreen<MachineScreenHandler> {
	// A path to the gui texture. In this example we use the texture from the dispenser

	//private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/dispenser.png");
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/furnace.png");
	private static final Identifier FIRE_TEXTURE = new Identifier("container/furnace/lit_progress");
	private static final Identifier ARROW_TEXTURE = new Identifier("container/furnace/burn_progress");


	public MachineScreen(MachineScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		//RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		graphics.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

		int fuelTextureLevel = MathHelper.ceil(MathHelper.clamp((float) this.handler.machineData.get(1) / (float) this.handler.machineData.get(0), 0.0F, 1.0F) * 13.0F) + 1;
		Pseudoscience.LOGGER.info("value: " + this.handler.machineData.get(1)  + " level: " + fuelTextureLevel);
		int progressTextureLevel = 10;
		graphics.drawGuiTexture(FIRE_TEXTURE, 14, 14, 0, 14 - fuelTextureLevel, 56 + x, 36 + 14 + y - fuelTextureLevel, 14, fuelTextureLevel);
		graphics.drawGuiTexture(ARROW_TEXTURE, 24, 16, 0, 0, 79 + x, 34 + y, progressTextureLevel, 16);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void init() {
		super.init();
		// Center the title
		titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
	}
}
