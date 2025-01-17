package net.zuiron.photosynthesis.mixin.Animals;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.fluid.ModFluids;
import net.zuiron.photosynthesis.item.ModItems;
import net.zuiron.photosynthesis.util.ModUtil;
import net.zuiron.photosynthesis.util.getCustomVarsPassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(CowEntity.class)
public abstract class ModCowEntity extends AnimalEntity {

    @Unique
    public int mod_Milk = 0; //this is 4 buckets worth of milk. each grass,hay,straw,food(TMR). adds +1 every tick if above 50%.
    @Unique
    protected final int mod_Milk_max = 96000; //24000 = one bucket.

    @Unique
    public int mod_Manure = 0;
    @Unique
    protected final int mod_Manure_max = 24000; //drop every mc day.

    protected ModCowEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void mobTick() {
        //String transkey = Objects.requireNonNull(this.getWorld().getEntityById(this.getId())).getType().getTranslationKey();

        //50% if actually ticking. making food last what, 7+7 days on average? so 14 mc days.
        /*if (getWorld().random.nextInt(2) != 0) {
            return;
        }*/
        int mod_Water = ((getCustomVarsPassiveEntity) this).getMod_Water();
        int mod_Water_max = ((getCustomVarsPassiveEntity) this).getMod_Water_max();

        int mod_Grass = ((getCustomVarsPassiveEntity) this).getMod_Grass();
        int mod_Grass_max = ((getCustomVarsPassiveEntity) this).getMod_Grass_max();

        int mod_Hay = ((getCustomVarsPassiveEntity) this).getMod_Hay();
        int mod_Hay_max = ((getCustomVarsPassiveEntity) this).getMod_Hay_max();

        int mod_Straw = ((getCustomVarsPassiveEntity) this).getMod_Straw();
        int mod_Straw_max = ((getCustomVarsPassiveEntity) this).getMod_Straw_max();

        int mod_Food = ((getCustomVarsPassiveEntity) this).getMod_Food();
        int mod_Food_max = ((getCustomVarsPassiveEntity) this).getMod_Food_max();

        //Food Control -------------------------------------------------------------------------------------------------
        //if(Objects.equals(transkey, "entity.minecraft.cow")) {
            //remove food and waters.
            /*if(mod_Water > 0)  { this.mod_Water--; }
            if(mod_Grass > 0)  { this.mod_Grass--; }
            if(mod_Hay > 0)    { this.mod_Hay--;   }
            if(mod_Straw > 0)  { this.mod_Straw--; }
            if(mod_Food > 0)   { this.mod_Food--;  } //for cows. TMR. total mixed rations.*/

            //remove food and waters.
            if(mod_Water > 0)  { ((getCustomVarsPassiveEntity) this).setMod_Water(mod_Water-1); }
            if(mod_Grass > 0)  { ((getCustomVarsPassiveEntity) this).setMod_Grass(mod_Grass-1); }
            if(mod_Hay > 0)    { ((getCustomVarsPassiveEntity) this).setMod_Hay(mod_Hay-1);   }
            if(mod_Straw > 0)  { ((getCustomVarsPassiveEntity) this).setMod_Straw(mod_Straw-1); }
            if(mod_Food > 0)   { ((getCustomVarsPassiveEntity) this).setMod_Food(mod_Food-1);  } //for cows. TMR. total mixed rations.

            float cowProductivity = photosynthesis$getMilkProductivity(mod_Water, mod_Water_max, mod_Grass, mod_Grass_max, mod_Straw, mod_Straw_max, mod_Hay, mod_Hay_max, mod_Food, mod_Food_max);

            //livingTicks
            if(cowProductivity >= 75.0f) {
                ((getCustomVarsPassiveEntity) this).addMod_LivingTicks(1);
            }

            //milk control.
            if(!this.isBaby() && cowProductivity >= 25.0f && this.mod_Milk < this.mod_Milk_max) {
                this.mod_Milk++;
                if(cowProductivity >= 50.0f) { this.mod_Milk++; }
                if(cowProductivity >= 75.0f) { this.mod_Milk++; }
                if(cowProductivity >= 100.0f) { this.mod_Milk++; }
            }

            //Manure control.
            if(!this.getWorld().isClient) {
                if (this.mod_Manure < this.mod_Manure_max) {
                    mod_Manure++;
                }
                else if (this.mod_Manure == this.mod_Manure_max) {
                    this.playSound(SoundEvents.ENTITY_SNIFFER_DROP_SEED, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    if (mod_Straw > (mod_Straw_max / 2)) {
                        //drop more
                        if(this.isBaby()) { this.dropStack(new ItemStack(ModItems.MANURE,4)); }
                        else { this.dropStack(new ItemStack(ModItems.MANURE,8)); }
                    } else {
                        //drop less
                        if(this.isBaby()) { this.dropStack(new ItemStack(ModItems.MANURE,2)); }
                        else { this.dropStack(new ItemStack(ModItems.MANURE,4)); }
                    }
                    this.emitGameEvent(GameEvent.ENTITY_PLACE);
                    this.mod_Manure = 0;
                }
            }

            //damage missing water or food.
            if(mod_Water <= 0 && ModUtil.canAnimalsDieByThirst()) {
                this.damage(this.getDamageSources().dryOut(), 2);
            } else if (mod_Food <= 0 && ModUtil.canAnimalsDieByHunger()) {
                if (mod_Grass <= 0 || mod_Hay <= 0) {
                    this.damage(this.getDamageSources().starve(), 1);
                }
            }
        //}

        super.mobTick();
    }



    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("photosynthesis_milk",this.mod_Milk);
        nbt.putInt("photosynthesis_manure",this.mod_Manure);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.mod_Milk = nbt.getInt("photosynthesis_milk");
        this.mod_Manure = nbt.getInt("photosynthesis_manure");
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {

        long mod_LivingTicks = ((getCustomVarsPassiveEntity) this).getMod_LivingTicks();

        int mod_Water = ((getCustomVarsPassiveEntity) this).getMod_Water();
        int mod_Water_max = ((getCustomVarsPassiveEntity) this).getMod_Water_max();

        int mod_Grass = ((getCustomVarsPassiveEntity) this).getMod_Grass();
        int mod_Grass_max = ((getCustomVarsPassiveEntity) this).getMod_Grass_max();

        int mod_Hay = ((getCustomVarsPassiveEntity) this).getMod_Hay();
        int mod_Hay_max = ((getCustomVarsPassiveEntity) this).getMod_Hay_max();

        int mod_Straw = ((getCustomVarsPassiveEntity) this).getMod_Straw();
        int mod_Straw_max = ((getCustomVarsPassiveEntity) this).getMod_Straw_max();

        int mod_Food = ((getCustomVarsPassiveEntity) this).getMod_Food();
        int mod_Food_max = ((getCustomVarsPassiveEntity) this).getMod_Food_max();

        if (!player.getWorld().isClient()) {

            //get some data.
            if (player.isPlayer()) {
                if (player.getStackInHand(hand).isOf(Items.STICK)) {

                    String entityname = this.getWorld().getEntityById(this.getId()).getName().getString(); //gets name, or if tagged tag name.
                    //String transkey = Objects.requireNonNull(this.getWorld().getEntityById(this.getId())).getType().getTranslationKey();
                    String string = "Entity: "+entityname + " \n";

                    float cowProductivity = photosynthesis$getMilkProductivity(mod_Water, mod_Water_max, mod_Grass, mod_Grass_max, mod_Straw, mod_Straw_max, mod_Hay, mod_Hay_max, mod_Food, mod_Food_max);

                    //if(Objects.equals(transkey, "entity.minecraft.cow")) {
                        string += "LivingTicks: "+mod_LivingTicks+" \n";

                        if(this.isBaby()) {
                            if(cowProductivity >= 75.0f) {
                                string += "Growing: TRUE \n";
                            } else {
                                string += "Growing: FALSE \n";
                            }
                        }

                        string += "Water: "+mod_Water+"/"+mod_Water_max+" \n";
                        string += "Grass: "+mod_Grass+"/"+mod_Grass_max+" \n";
                        string += "Hay: "+mod_Hay+"/"+mod_Hay_max+" \n";
                        string += "Straw: "+mod_Straw+"/"+mod_Straw_max+" \n";
                        string += "TMR: "+mod_Food+"/"+mod_Food_max+" \n";
                        string += "Milk: "+this.mod_Milk+"/"+mod_Milk_max+", Buckets: "+ photosynthesis$getAvailBucketsMilk()+" \n";
                        string += "Manure: "+this.mod_Manure+"/"+this.mod_Manure_max+" \n";
                        string += "Productivity: "+ cowProductivity +"%";
                    //}


                    player.sendMessage(Text.literal(string),false);
                } else if (player.getStackInHand(hand).isOf(Items.ROTTEN_FLESH)) {
                    //this.mod_Milk = this.mod_Milk_max;
                    //((getCustomVarsPassiveEntity) this).setMod_Grass(mod_Grass-24000);
                    //((getCustomVarsPassiveEntity) this).addMod_LivingTicks(480000);
                }
            }
        }

        //MILK BUCKET STUFF
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.BUCKET) && !this.isBaby() && this.mod_Milk < 24000) {
            if(!player.getWorld().isClient) {
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_COW_HURT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                player.sendMessage(Text.literal("Not Enough Milk: " + this.mod_Milk + "/" + this.mod_Milk_max + ", " + photosynthesis$getAvailBucketsMilk() + " Buckets. \n Productivity: "+ photosynthesis$getMilkProductivity(mod_Water, mod_Water_max, mod_Grass, mod_Grass_max, mod_Straw, mod_Straw_max, mod_Hay, mod_Hay_max, mod_Food, mod_Food_max)+"%"), false);
            }
        }
        if (itemStack.isOf(Items.BUCKET) && !this.isBaby() && this.mod_Milk >= 24000) {
            if(!player.getWorld().isClient) {
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_COW_MILK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            }
            ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, ModFluids.MILK_BUCKET.getDefaultStack()); //we give our own milk bucket instead
            player.getItemCooldownManager().set(itemStack2.getItem(), 40); //fixes weird issue with reactivating immediately.
            player.setStackInHand(hand, itemStack2);
            this.mod_Milk -= 24000;

            if(!player.getWorld().isClient) {
                player.sendMessage(Text.literal("Milk: " + this.mod_Milk + "/" + this.mod_Milk_max + ", " + photosynthesis$getAvailBucketsMilk() + " Buckets Left. \n Productivity: "+ photosynthesis$getMilkProductivity(mod_Water, mod_Water_max, mod_Grass, mod_Grass_max, mod_Straw, mod_Straw_max, mod_Hay, mod_Hay_max, mod_Food, mod_Food_max)+"%"), false);
            }

            //return ActionResult.success(this.getWorld().isClient);
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        } else {
            //return super.interactMob(player, hand);
            cir.setReturnValue(super.interactMob(player, hand));
        }

        /*ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.BUCKET) && !this.isBaby() && this.milkablecooldown < cooldownticks) {
            player.playSound(SoundEvents.ENTITY_COW_AMBIENT, 1.0F, 1.0F);
            if(this.milkablecooldown != 0) {
                Photosynthesis.LOGGER.info("cow ticks:" + this.milkablecooldown + "/"+cooldownticks);
            }
        }
        if (itemStack.isOf(Items.BUCKET) && !this.isBaby() && this.milkablecooldown >= cooldownticks) {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, ModFluids.MILK_BUCKET.getDefaultStack()); //we give our own milk bucket instead
            player.getItemCooldownManager().set(itemStack2.getItem(), 40); //fixes weird issue with reactivating immediately.
            player.setStackInHand(hand, itemStack2);
            this.milkablecooldown = 0;
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        } else {
            cir.setReturnValue(super.interactMob(player, hand));
        }
        cir.cancel(); //do not run vanilla code.*/



        /*int water = ((getCustomVarsPassiveEntity) this).getMod_Water(); // WORKING
        Photosynthesis.LOGGER.info("water test: "+water);*/
    }

    @Unique
    public int photosynthesis$getAvailBucketsMilk() {
        if(this.mod_Milk >= (24000 * 4)) { return 4; }
        if(this.mod_Milk >= (24000 * 3)) { return 3; }
        if(this.mod_Milk >= (24000 * 2)) { return 2; }
        if(this.mod_Milk >= 24000)       { return 1; }

        return 0;
    }

    @Unique
    public float photosynthesis$getMilkProductivity(int mod_Water, int mod_Water_max, int mod_Grass, int mod_Grass_max, int mod_Straw, int mod_Straw_max, int mod_Hay, int mod_Hay_max, int mod_Food, int mod_Food_max) {
        float productivity = 0.0f;

        if(mod_Water >= (mod_Water_max/2)) {
            if(mod_Grass > (mod_Grass_max/2)) { productivity += 25f; }
            if(mod_Hay   > (mod_Hay_max/2)  ) { productivity += 25f; }
            if(mod_Straw > (mod_Straw_max/2)) { productivity += 25f; }
            if(mod_Food  > (mod_Food_max/2) ) { productivity += 25f; }
        }

        return productivity;
    }
}
