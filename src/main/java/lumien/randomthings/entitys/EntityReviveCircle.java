package lumien.randomthings.entitys;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityReviveCircle extends Entity
{
	public int age;
	EntitySoul toRevive;
	EntityPlayer reviver;

	public EntityReviveCircle(World worldObj)
	{
		super(worldObj);

		age = 0;
		this.noClip = true;
		this.renderDistanceWeight = 5;
		this.ignoreFrustumCheck = true;
	}

	public EntityReviveCircle(World worldObj, EntityPlayer reviver, double posX, double posY, double posZ, EntitySoul toRevive)
	{
		super(worldObj);

		this.setPosition(posX, posY, posZ);
		this.toRevive = toRevive;
		this.noClip = true;
		this.renderDistanceWeight = 5;
		this.ignoreFrustumCheck = true;
		this.reviver = reviver;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		age++;

		if (!worldObj.isRemote)
		{
			if (this.reviver == null || this.reviver.isDead || this.reviver.getDistanceSq(this.getPosition()) > 25 || this.reviver.dimension != this.dimension || toRevive == null || toRevive.isDead)
			{
				this.kill();
			}

			if (this.worldObj.getTotalWorldTime() % 20 == 0)
			{
				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(toRevive.playerName);
				if (player == null)
				{
					this.kill();
				}
			}

			if (this.age >= 200 && this.worldObj.getTotalWorldTime() % 10 == 0)
			{
				reviver.attackEntityFrom(DamageSource.magic, 1f);
			}

			if (this.age >= 400)
			{
				toRevive.setDead();
				this.setDead();

				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(toRevive.playerName);
				if (player != null)
				{
					if (player.getHealth() <= 0)
					{
						EntityPlayerMP revived = player.playerNetServerHandler.playerEntity = MinecraftServer.getServer().getConfigurationManager().recreatePlayerEntity(player, 0, false);
						if (revived.worldObj.provider.getDimensionId() != this.dimension)
						{
							MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(revived, this.worldObj.provider.getDimensionId(), new Teleporter((WorldServer) this.worldObj));
						}
						revived.playerNetServerHandler.setPlayerLocation(posX, posY, posZ, revived.rotationYaw, revived.rotationPitch);
						revived.setPositionAndUpdate(posX, posY, posZ);
					}
				}
			}
		}
		else
		{
			if (this.age >= 200)
			{
				worldObj.spawnParticle(EnumParticleTypes.REDSTONE, this.posX + Math.random() - 0.5f, this.posY + Math.random(), this.posZ + Math.random() - 0.5f, 0, 0, 0, 1);
			}
		}
	}

	@Override
	public void setDead()
	{
		super.setDead();

		if (this.worldObj.isRemote)
		{
			for (int i = 0; i < 100; i++)
			{
				worldObj.spawnParticle(EnumParticleTypes.REDSTONE, this.posX + Math.random() * 3 - 1.5f, this.posY + Math.random(), this.posZ + Math.random() * 3 - 1.5f, 0, 0, 0, 1);
			}
		}
	}

	@Override
	protected void entityInit()
	{
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		int soulID = nbt.getInteger("soul");

		Entity entity = worldObj.getEntityByID(soulID);

		if (entity != null && entity instanceof EntitySoul)
		{
			this.toRevive = (EntitySoul) entity;
		}
		else
		{
			this.kill();
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("soul", toRevive.getEntityId());
	}

}
