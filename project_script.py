import pandas as pd
import matplotlib.pyplot as plt

# Load the dataset
data = pd.read_csv('all_seasons.csv')

# Preview the dataset
print("Dataset Preview:")
print(data.head())

# ----- Analysis 1: Top 5 Tallest Players -----
# Group data by player_name and retain the max height only for numeric columns
unique_tallest = data.groupby('player_name', as_index=False).agg({
    'player_height': 'max',
    'team_abbreviation': 'first'  # Keep the first team abbreviation
})

# Get the top 5 tallest players
top_tallest = unique_tallest.nlargest(5, 'player_height')

print("Top 5 Tallest Players:")
print(top_tallest[['player_name', 'player_height', 'team_abbreviation']])

# Horizontal bar chart of the top 5 tallest players with values
ax = top_tallest.plot(x='player_name', y='player_height', kind='barh', title='Top 5 Tallest Players', color='skyblue')
plt.xlabel("Height (cm)")
plt.ylabel("Player Name")
for i, v in enumerate(top_tallest['player_height']):
    ax.text(v + 1, i, f"{v:.2f}", color='black', va='center')  # Add values
plt.show()

# ----- Analysis 2: Top 5 Players by Points Per Game -----
top_scorers = data.nlargest(5, 'pts')
print("Top 5 Scorers:")
print(top_scorers[['player_name', 'pts', 'team_abbreviation']])

# Bar chart of the top 5 scorers with values
ax = top_scorers.plot(x='player_name', y='pts', kind='bar', title='Top 5 Scorers', color='orange')
plt.xlabel("Player Name")
plt.ylabel("Points Per Game")
for i, v in enumerate(top_scorers['pts']):
    ax.text(i, v + 0.5, f"{v:.2f}", color='black', ha='center')  # Add values
plt.show()

# ----- Analysis 3: Team Comparisons - Average Points -----
team_avg_pts = data.groupby('team_abbreviation')['pts'].mean().sort_values(ascending=False).head(5)
print("Top 5 Teams by Average Points:")
print(team_avg_pts)

# Bar chart of average points by team with values
ax = team_avg_pts.plot(kind='bar', title='Top 5 Teams by Average Points', color='green')
plt.xlabel("Team")
plt.ylabel("Average Points")
for i, v in enumerate(team_avg_pts):
    ax.text(i, v + 0.5, f"{v:.2f}", color='black', ha='center')  # Add values
plt.show()

# ----- Analysis 4: Player Age Distribution -----
print("Player Age Distribution:")
ax = data['age'].plot(kind='hist', bins=10, title='Player Age Distribution', color='purple', edgecolor='black')
plt.xlabel("Age")
plt.ylabel("Frequency")
# Optional: Add frequency values above each bar in the histogram
n, bins, patches = plt.hist(data['age'], bins=10, color='purple', edgecolor='black')
for i in range(len(patches)):
    ax.text(bins[i] + (bins[1] - bins[0]) / 2, n[i] + 0.5, int(n[i]), ha='center', color='black')
plt.show()