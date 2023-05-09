package com.carterz30cal.items;

public class ItemReq 
{
	public String item;
	public int amount;
	public int coins;
	
	public ItemReq(String item, int amount)
	{
		this.item = item;
		this.amount = amount;
		this.coins = 0;
	}
	
	public ItemReq(String item, int amount, int coins)
	{
		this.item = item;
		this.amount = amount;
		this.coins = coins;
	}
}
