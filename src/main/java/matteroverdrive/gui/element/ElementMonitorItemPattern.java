/*
 * This file is part of Matter Overdrive
 * Copyright (c) 2015., Simeon Radivoev, All rights reserved.
 *
 * Matter Overdrive is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Matter Overdrive is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Matter Overdrive.  If not, see <http://www.gnu.org/licenses>.
 */

package matteroverdrive.gui.element;

import matteroverdrive.Reference;
import matteroverdrive.container.IButtonHandler;
import matteroverdrive.data.ItemPattern;
import matteroverdrive.gui.MOGuiBase;
import matteroverdrive.util.RenderUtils;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by Simeon on 4/29/2015.
 */
public class ElementMonitorItemPattern extends ElementItemPattern
{
    IButtonHandler buttonHandler;
    boolean expanded;

    public ElementMonitorItemPattern(MOGuiBase gui, ItemPattern pattern, IButtonHandler buttonHandler)
    {
        super(gui, pattern,"big",22,22);
        this.buttonHandler = buttonHandler;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY)
    {
        RenderUtils.renderStack(this.posX + 3, this.posY + 3, this.itemStack);

        if (!this.expanded && this.amount > 0)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 100);
            this.gui.drawCenteredString(this.getFontRenderer(), Integer.toString(this.amount), this.posX + 17, this.posY + 12, 0xFFFFFF);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float gameTicks)
    {
        super.drawBackground(mouseX,mouseY,gameTicks);

        if (this.expanded)
        {
            this.ApplyColor();
            MOElementButton.NORMAL_TEXTURE.render(this.posX + 22, this.posY + 2, 18, 18);
            this.getFontRenderer().drawString(EnumChatFormatting.BOLD + "+", this.posX + 28, this.posY + 7, Reference.COLOR_MATTER.getColor());
            this.ApplyColor();
            MOElementButton.NORMAL_TEXTURE.render(this.posX + 22, this.posY + 22, 18, 18);
            this.getFontRenderer().drawString(EnumChatFormatting.BOLD + "-", this.posX + 28, this.posY + 28, Reference.COLOR_MATTER.getColor());
            this.ApplyColor();
            MOElementButton.HOVER_TEXTURE_DARK.render(this.posX + 2, this.posY + 22, 18, 18);
            this.gui.drawCenteredString(this.getFontRenderer(), Integer.toString(this.amount), this.posX + 11, this.posY + 28, Reference.COLOR_MATTER.getColor());
        }
        this.ResetColor();
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int mouseButton)
    {
        if (this.expanded)
        {
            if (mouseX < this.posX +22 && mouseY < this.posY + 22)
            {
                this.setExpanded(false);
            }
            else if (mouseX > this.posX + 24 && mouseY < this.posY + 22)
            {
                if(this.amount == 64) {
                    this.amount = 99;
                } else {
                    this.amount = Math.min(this.amount + (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) | Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 16 : 1), 64);
                }
            }
            else if (mouseX > this.posX + 24 && mouseY > this.posY + 24)
            {
                if(this.amount == 99) {
                    this.amount = 64;
                } else {
                    this.amount = Math.max(this.amount - (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) | Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 16 : 1), 0);
                }
            }
        }
        else
        {
            this.setExpanded(true);
        }
        return true;
    }

    public void setExpanded(boolean expanded)
    {
        if (!expanded)
        {
            this.expanded = expanded;
            this.setSize(22, 22);
        }
        else
        {
            this.expanded = expanded;
            this.setSize(44, 44);
        }
    }
}
