resource "aws_dynamodb_table" "history" {
  name = "GucocaBroadcastHistory"
  read_capacity = 2
  write_capacity = 1
  hash_key = "Slug"
  range_key = "BroadcastDate"
  attribute {
    name = "BroadcastDate"
    type = "N" // unix epoch
  }
  attribute {
    name = "Slug"
    type = "S" // the slug of the story that was published
  }
}
