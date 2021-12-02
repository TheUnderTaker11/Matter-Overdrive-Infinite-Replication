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

package matteroverdrive.gui;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.Reference;
import matteroverdrive.api.network.MatterNetworkTaskState;
import matteroverdrive.container.ContainerPatternMonitor;
import matteroverdrive.container.MOBaseContainer;
import matteroverdrive.data.ItemPattern;
import matteroverdrive.gui.element.*;
import matteroverdrive.gui.pages.PageTasks;
import matteroverdrive.network.packet.server.PacketPatternMonitorCommands;
import matteroverdrive.network.packet.server.PacketRemoveTask;
import matteroverdrive.proxy.ClientProxy;
import matteroverdrive.tile.TileEntityMachinePatternMonitor;
import matteroverdrive.util.MOStringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Simeon on 4/26/2015.
 */
public class GuiPatternMonitor extends MOGuiNetworkMachine<TileEntityMachinePatternMonitor>
{
    MOElementButton refreshButton;
    MOElementButton requestButton;
    ElementPatternsGrid elementGrid;
    PageTasks pageTasks;
    MOElementTextField searchField;

    public GuiPatternMonitor(InventoryPlayer inventoryPlayer, TileEntityMachinePatternMonitor machine)
    {
        super(new ContainerPatternMonitor(inventoryPlayer,machine), machine);
        this.name = "pattern_monitor";
        this.refreshButton = new MOElementButton(this,this,6,45,"Refresh",0,0,22,0,22,22, "");
        this.refreshButton.setTexture(Reference.PATH_GUI_ITEM + "refresh.png", 44, 22);
        this.refreshButton.setToolTip(MOStringHelper.translateToLocal("gui.tooltip.button.refresh"));
        this.requestButton = new MOElementButton(this,this,6,75,"Request",0,0,22,0,22,22,"");
        this.requestButton.setTexture(Reference.PATH_GUI_ITEM + "request.png",44,22);
        this.requestButton.setToolTip(MOStringHelper.translateToLocal("gui.tooltip.button.request"));
        this.elementGrid = new ElementPatternsGrid(this,48,40,160,110);
        this.searchField = new MOElementTextField(this,41,26,167,14);
        this.slotsList.addElement(this.refreshButton);
        this.slotsList.addElement(this.requestButton);
        this.elementGrid.updateStackList(machine.getGuiPatterns());
    }

    @Override
    public void registerPages(MOBaseContainer container,TileEntityMachinePatternMonitor machine)
    {
        super.registerPages(container, machine);

        this.pageTasks = new PageTasks(this,0,0,this.xSize,this.ySize,machine.getTaskQueue((byte) 0));
        this.pageTasks.setName("Tasks");
        this.AddPage(this.pageTasks, ClientProxy.holoIcons.getIcon("page_icon_tasks"), MOStringHelper.translateToLocal("gui.tooltip.page.tasks")).setIconColor(Reference.COLOR_MATTER);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        //this.addElement(refreshButton);
        //this.addElement(requestButton);
        this.pages.get(0).addElement(this.elementGrid);
        this.pages.get(0).addElement(this.searchField);
        this.AddHotbarPlayerSlots(this.inventorySlots, this);
    }

    @Override
    public void handleElementButtonClick(MOElementBase element, String buttonName, int mouseButton)
    {
        super.handleElementButtonClick(element,buttonName,mouseButton);
        if (buttonName.equals("Refresh"))
        {
            MatterOverdrive.packetPipeline.sendToServer(new PacketPatternMonitorCommands(this.machine,0,null));
        }
        else if (buttonName.equals("Request"))
        {
            List<ItemPattern> requestList = new ArrayList<>();
            for (int i = 0;i < this.elementGrid.getElements().size();i++)
            {
                if (this.elementGrid.getElements().get(i) instanceof ElementMonitorItemPattern)
                {
                    ElementMonitorItemPattern itemPattern = (ElementMonitorItemPattern)this.elementGrid.getElements().get(i);

                    if (itemPattern.getAmount() > 0)
                    {
                        ItemPattern pattern = itemPattern.getPattern().copy();
                        if(itemPattern.getAmount() == 99) {
                            pattern.setCount(Integer.MAX_VALUE - 1);
                            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("You have requested an 'infinite' amount of an item. It will continue replicating that same item essentially forever. You can stop this from within the Matter Replicator GUI!"));
                        }else {
                            pattern.setCount(itemPattern.getAmount());
                        }

                        requestList.add(pattern);
                        itemPattern.setAmount(0);
                    }
                    else
                    {
                        itemPattern.setExpanded(false);
                    }
                }
            }

            if (requestList.size() > 0)
            {
                MatterOverdrive.packetPipeline.sendToServer(new PacketPatternMonitorCommands(this.machine, PacketPatternMonitorCommands.COMMAND_REQUEST, requestList));
            }
        }
        else if (buttonName.equals("DropTask"))
        {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setInteger("TaskID",mouseButton);
            MatterOverdrive.packetPipeline.sendToServer(new PacketRemoveTask(this.machine,mouseButton,(byte)0, MatterNetworkTaskState.INVALID));
        }
    }

    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();

        if (this.machine.needsRefresh())
        {
            this.elementGrid.updateStackList(this.machine.getGuiPatterns());
            this.machine.forceSearch(false);
        }

        this.elementGrid.setFilter(this.searchField.getText());
    }
}
