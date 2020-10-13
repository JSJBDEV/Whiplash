package gd.rf.acro.whiplash.mixin;

import gd.rf.acro.whiplash.BuchUtils;
import gd.rf.acro.whiplash.Whiplash;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V", shift = At.Shift.AFTER), cancellable = true
    )
    private void renderItem(ItemStack stack, Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if(stack.getItem()== Whiplash.DYNAMIC_SPELL_BOOK_ITEM)
        {
            VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTextureForStack(stack)));
            MatrixStack.Entry tos = matrices.peek();
            Matrix3f normal = tos.getNormal();
            Matrix4f tr = tos.getModel();
            //topright, topleft, bottomleft, bottomright
            buffer.vertex(tr, 0.7f, 0.9f, 0.44f).color(1f, 1f, 1f, 1f).texture(0f, 0f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
            buffer.vertex(tr, 0.2f, 0.9f, 0.44f).color(1f, 1f, 1f, 1f).texture(1f, 0f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
            buffer.vertex(tr, 0.2f, 0.1f, 0.44f).color(1f, 1f, 1f, 1f).texture(1f, 1f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
            buffer.vertex(tr, 0.7f, 0.1f, 0.44f).color(1f, 1f, 1f, 1f).texture(0f, 1f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();

            buffer.vertex(tr, 0.2f, 0.9f, 0.44f).color(1f, 1f, 1f, 1f).texture(0f, 0f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
            buffer.vertex(tr, 0.7f, 0.9f, 0.44f).color(1f, 1f, 1f, 1f).texture(1f, 0f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
            buffer.vertex(tr, 0.7f, 0.1f, 0.44f).color(1f, 1f, 1f, 1f).texture(1f, 1f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
            buffer.vertex(tr, 0.2f, 0.1f, 0.44f).color(1f, 1f, 1f, 1f).texture(0f, 1f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
            ci.cancel();
            matrices.pop();
        }
    }
    private HashMap<byte[],Identifier> memory = new HashMap<>();
    private static final String[] elements = {"fire","water","earth"};
    private static final String[] styles = {"ball","beam","selforb","orb"};
    private Identifier getTextureForStack(ItemStack stack)
    {

        if(stack.hasTag()) //if the stack has been assigned a texture it will have a tag, and at no other time.
        {
            CompoundTag tag = stack.getTag();
           if(!memory.containsKey(tag.getByteArray("tex"))) //if the stack has been assigned a texture, but its not in memory
           {
               try{
                   InputStream is = new ByteArrayInputStream(tag.getByteArray("tex"));
                   Identifier id = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("whiplash",new NativeImageBackedTexture(NativeImage.read(is)));
                   memory.put(tag.getByteArray("tex"),id);
                   return id;
               }catch (IOException e){e.printStackTrace();}
           }
           else //basically any frame after the first one
           {
               return memory.get(tag.getByteArray("tex"));
           }
        }
        else
        {
            CompoundTag tag = new CompoundTag(); //if the stack does not have a texture assigned, the texture cannot be in memory
            try{
                BuchUtils book = new BuchUtils();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(book.getImage(),"png",os);
                tag.putByteArray("tex",os.toByteArray());
                tag.putString("element",elements[RandomUtils.nextInt(0,elements.length)]);
                tag.putString("style",styles[RandomUtils.nextInt(0,styles.length)]);
                tag.putInt("power",RandomUtils.nextInt(0,10));
                tag.putInt("drain",RandomUtils.nextInt(0,6));
                tag.putInt("effect",RandomUtils.nextInt(0,25));
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                Identifier id = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("whiplash",new NativeImageBackedTexture(NativeImage.read(is)));
                memory.put(os.toByteArray(),id);
                stack.setTag(tag);
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeItemStack(stack);
                passedData.writeByteArray(tag.getByteArray("tex"));
                passedData.writeString(tag.getString("element"));
                passedData.writeString(tag.getString("style"));
                passedData.writeInt(tag.getInt("power"));
                passedData.writeInt(tag.getInt("drain"));
                passedData.writeInt(tag.getInt("effect"));
                ClientSidePacketRegistry.INSTANCE.sendToServer(Whiplash.SEND_SPELL_DATA,passedData);

                return id;
            }catch (IOException e){e.printStackTrace();}
        }
        return new Identifier("minecraft","stone");
    }
}
