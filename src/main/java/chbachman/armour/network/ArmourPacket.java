package chbachman.armour.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import chbachman.api.IModularItem;
import chbachman.api.IUpgrade;
import chbachman.armour.gui.IInputHandler;
import chbachman.armour.util.ArmourSlot;
import chbachman.armour.util.NBTHelper;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;

public class ArmourPacket extends PacketCoFHBase {
    
    public static void initialize() {
        
        PacketHandler.instance.registerPacket(ArmourPacket.class);
    }
    
    public enum PacketTypes {
        
        BUTTON(), KEYTYPED(), ENTITYJOINWORLD();
        
    }
    
    @Override
    public void handlePacket(EntityPlayer player, boolean isServer) {
        
        try {
            int type = this.getByte();
            
            switch (PacketTypes.values()[type]) {
            
            case BUTTON:
                this.handleButtonPressed(player);
                break;
            case KEYTYPED:
                this.handleKeyTyped(player);
                break;
            case ENTITYJOINWORLD:
                this.handleEntityJoinWorld(player);
                break;
                
            default:
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the packet for the given type.
     * @param type
     * @return
     */
    public static PacketCoFHBase getPacket(PacketTypes type) {
        
        return new ArmourPacket().addByte(type.ordinal());
    }
    
    public void handleKeyTyped(EntityPlayer player){
    	
        Container container = player.openContainer;
        
        if (container instanceof IInputHandler) {
            IInputHandler inputHandler = (IInputHandler) container;
            
            inputHandler.onKeyTyped(this, (char) this.getShort(), this.getInt());
        }
    }
    
    public void handleButtonPressed(EntityPlayer player) {
        
        Container container = player.openContainer;
        
        if (container instanceof IInputHandler) {
            IInputHandler inputHandler = (IInputHandler) container;
            
            inputHandler.onButtonClick(this, this.getString());
        }
    }
    
    public void handleEntityJoinWorld(EntityPlayer player) {
        
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof IModularItem) {
                
            	IModularItem armour = (IModularItem) stack.getItem();
                
                for (IUpgrade upgrade : NBTHelper.getNBTUpgradeList(stack.stackTagCompound)) {
                    upgrade.onEquip(player.worldObj, player, stack, ArmourSlot.getArmourSlot(armour.getSlot()), armour.getLevel(stack));
                }
                
            }
        }
    }
    
}
