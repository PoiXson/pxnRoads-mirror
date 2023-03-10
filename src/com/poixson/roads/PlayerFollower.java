package com.poixson.roads;

import java.util.UUID;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.poixson.commonmc.tools.plugin.xListener;
import com.poixson.roads.builders.RoadBuilder;


public class PlayerFollower extends xListener<RoadsPlugin> {

	protected final Player player;
	protected final UUID   uuid;

	public boolean snap = false;

	public final String world;
	public final int start_x;
	public final int start_y;
	public final int start_z;
	public final BlockFace direction;

	public RoadBuilder builder = null;



	public PlayerFollower(final RoadsPlugin plugin, final Player player) {
		super(plugin);
		this.player = player;
		this.uuid   = player.getUniqueId();
		final Location loc = player.getLocation();
		this.world = loc.getWorld().getName();
		this.start_x = loc.getBlockX();
		this.start_y = loc.getBlockY();
		this.start_z = loc.getBlockZ();
		this.direction = player.getFacing();
	}



	public boolean isPlayer(final Player player) {
		return this.isPlayer(player.getUniqueId());
	}
	public boolean isPlayer(final UUID uuid) {
		return this.uuid.equals(uuid);
	}



	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (!this.isPlayer(player))
			return;
		final Location from = event.getFrom();
		final Location to   = event.getTo();
		// stop following outside of world
		final World world = to.getWorld();
		if (!this.world.equals(world)) {
			this.plugin.stopFollower(this.player);
			return;
		}
		// location changed
		final int total =
				(to.getBlockX() - from.getBlockX()) +
				(to.getBlockZ() - from.getBlockZ());
		if (total == 0) return;
		if (total == 1) {
			this.onMove(world, to);
		} else {
//TODO
			this.onMove(world, to);
		}
	}
	protected void onMove(final World world, final Location to) {
		final Axis axis = LocationUtils.DirectionToAxis(this.direction);
		// build road
		if (this.snap) {
			switch (axis) {
			case X: {
				final Location loc = world.getBlockAt(to.getBlockX(), this.start_y, this.start_z).getLocation();
				this.builder.build(axis, loc);
				break;
			}
			case Z: {
				final Location loc = world.getBlockAt(this.start_x, this.start_y, to.getBlockZ()).getLocation();
				this.builder.build(axis, loc);
				break;
			}
			default: throw new RuntimeException("Invalid road direction");
			}
		} else {
			this.builder.build(axis, to);
		}
	}



}
