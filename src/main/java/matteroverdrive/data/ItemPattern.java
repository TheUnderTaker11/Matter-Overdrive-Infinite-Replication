package matteroverdrive.data;

import io.netty.buffer.ByteBuf;
import matteroverdrive.util.MatterDatabaseHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Simeon on 12/27/2015.
 */
public class ItemPattern
{
    private int itemID;
    private int damage;
    private int count;
    private int progress;

    public ItemPattern()
    {

    }
    public ItemPattern(ItemStack itemStack)
    {
        this(itemStack,0);
    }
    public ItemPattern(ItemStack itemStack,int progress)
    {
        this(Item.getIdFromItem(itemStack.getItem()),itemStack.getItemDamage(),progress);
    }
    public ItemPattern(int itemID)
    {
        this(itemID,0,0);
    }
    public ItemPattern(int itemID,int damage)
    {
        this(itemID,damage,0);
    }
    public ItemPattern(int itemID,int damage,int progress)
    {
        this.itemID = itemID;
        this.damage = damage;
        this.progress = progress;
    }

    public ItemPattern(NBTTagCompound tagCompound)
    {
        this.readFromNBT(tagCompound);
    }

    public ItemPattern(ByteBuf byteBuf)
    {
        this.readFromBuffer(byteBuf);
    }

    public ItemStack toItemStack(boolean withInfo)
    {
        ItemStack itemStack = new ItemStack(Item.getItemById(this.itemID));
        itemStack.setItemDamage(this.damage);
        if (withInfo)
        {
            itemStack.setTagCompound(new NBTTagCompound());
            itemStack.getTagCompound().setByte(MatterDatabaseHelper.PROGRESS_TAG_NAME,(byte) this.progress);
        }
        return itemStack;
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setShort("id",(short)this.itemID);
        nbtTagCompound.setByte(MatterDatabaseHelper.PROGRESS_TAG_NAME,(byte) this.progress);
        nbtTagCompound.setShort("Damage",(short)this.damage);
        nbtTagCompound.setInteger("Count", this.count);
    }

    public void writeToBuffer(ByteBuf byteBuf)
    {
        byteBuf.writeShort(this.itemID);
        byteBuf.writeByte(this.progress);
        byteBuf.writeShort(this.damage);
        byteBuf.writeInt(this.count);
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        this.itemID = nbtTagCompound.getShort("id");
        this.progress = nbtTagCompound.getByte(MatterDatabaseHelper.PROGRESS_TAG_NAME);
        this.damage = nbtTagCompound.getShort("Damage");
        this.count = nbtTagCompound.getInteger("Count");
    }

    public void readFromBuffer(ByteBuf byteBuf)
    {
        this.itemID = byteBuf.readShort();
        this.progress = byteBuf.readByte();
        this.damage = byteBuf.readShort();
        this.count = byteBuf.readInt();
    }

    public int getItemID(){return this.itemID;}
    public int getProgress(){return this.progress;}
    public float getProgressF(){return (float)this.progress/(float)MatterDatabaseHelper.MAX_ITEM_PROGRESS;}
    public int getCount(){return this.count;}
    public int getDamage(){return this.damage;}
    public void setDamage(int damage){this.damage = damage;}
    public void setCount(int count){this.count = count;}
    public Item getItem(){return Item.getItemById(this.getItemID());}
    public boolean equals(ItemPattern pattern)
    {
        return this.getItemID() == pattern.getItemID() && this.getDamage() == pattern.getDamage();
    }
    @Override
    public boolean equals(Object object)
    {
        if (super.equals(object))
            return true;
        if (object instanceof ItemPattern)
            return this.equals((ItemPattern)object);
        return false;
    }
    public String getDisplayName()
    {
        return this.toItemStack(false).getDisplayName();
    }
    public ItemPattern copy()
    {
        ItemPattern pattern = new ItemPattern(this.itemID,this.damage,this.progress);
        pattern.setCount(this.count);
        return pattern;
    }
}
