resource "aws_dynamodb_table" "history" {
  name = "gucoca-history"
  read_capacity = 2
  write_capacity = 1
  hash_key = "slug"
  range_key = "broadcast-date"
  attribute {
    name = "broadcast-date"
    type = "N" // unix epoch
  }
  attribute {
    name = "slug"
    type = "S" // the slug of the story that was published
  }
}
